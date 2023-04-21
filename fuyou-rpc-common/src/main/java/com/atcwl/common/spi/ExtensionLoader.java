package com.atcwl.common.spi;

import cn.hutool.core.util.StrUtil;
import com.atcwl.common.annotation.FuyouRpcSPI;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-05-07 15:28
 **/
@Slf4j
public class ExtensionLoader<T> {
    /**
     * 扩展类实例缓存 {name: 扩展类实例}
     */
    private final Map<String, T> extensionsCache = new ConcurrentHashMap<>(8);

    /**
     * 扩展加载器实例缓存 {类型：加载器实例}
     */
    private static final Map<Class<?>, ExtensionLoader<?>> extensionLoaderCache = new ConcurrentHashMap<>(8);

    /**
     * 扩展类配置列表缓存 {type: {name, 扩展类}}
     * 存储扩展类名称：扩展类类型的对应关系，便于初始化对应扩展类实例
     */
    private final ExtensionHolder<Map<String, Class<?>>> extensionClassesCache = new ExtensionHolder<>();

    /**
     * 创建扩展实例类的锁缓存 {name: synchronized 持有的锁}
     */
    private final Map<String, Object> createExtensionLockMap = new ConcurrentHashMap<>(8);

    /**
     * 扩展类加载器的类型
     */
    private final Class<T> type;

    /**
     * 扩展类存放的目录地址
     */
    private static final String EXTENSION_PATH = "META-INF/fuyou-rpc/";

    /**
     * 默认扩展名缓存
     */
    private final String defaultNameCache;

    /**
     * 构造函数
     *
     * @param type 扩展类加载器的类型
     */
    private ExtensionLoader(Class<T> type) {
        this.type = type;
        FuyouRpcSPI annotation = type.getAnnotation(FuyouRpcSPI.class);
        defaultNameCache = annotation.value();
    }

    /**
     * 获取当前扩展类加载器所有加载到的spi实现类
     *这里的扩展类加载器是对应于某一个具体的SPI接口，其实就是你创建ExtensionClassLoader的泛型
     * 比如我们为Serializer接口创建了一个ExtensionClassLoader，那么可以利用该扩展类加载器来加载Serializer的所有扩展类
     * Serializer接口的第三方类
     * @return
     */
    public Map<String, Class<?>> getExtensionClassesCache() {
        //获取到当前SPI接口对应扩展类加载器
        Map<String, Class<?>> extensionClasses = extensionClassesCache.get();
        //若该扩展类加载器存在，则直接返回
        if (extensionClasses != null && extensionClasses.size() > 0) {
            return extensionClasses;
        }
        //不存在则需要创建，类加载器的创建过程需要保证线程安全，因此需要上锁
        synchronized (extensionClassesCache) {
            extensionClasses = extensionClassesCache.get();
            //类加载器需要保持单例，因此用到双端检查
            if (extensionClasses == null || extensionClasses.size() < 1) {
                extensionClasses = loadClassesFromResources();
                extensionClassesCache.set(extensionClasses);
            }
        }
        return extensionClasses;
    }

    /**
     * 获取对应类型的扩展加载器实例
     * 根据对应SPI接口类型获取对应扩展类加载器
     * @param type 扩展类加载器的类型
     * @return 扩展类加载器实例
     */
    public static <S> ExtensionLoader<S> getLoader(Class<S> type) {
        // 扩展类型必须是接口
        if (!type.isInterface()) {
            throw new IllegalStateException(type.getName() + " is not interface");
        }
        //判断该接口是不是SPI接口，所有的SPI接口都应该加上了指定的注解标识
        FuyouRpcSPI annotation = type.getAnnotation(FuyouRpcSPI.class);
        if (annotation == null) {
            throw new IllegalStateException(type.getName() + " has not @SimpleRpcSPI annotation.");
        }
        //获取该SPI接口对应的扩展类加载器
        ExtensionLoader<?> extensionLoader = extensionLoaderCache.get(type);
        if (extensionLoader != null) {
            //noinspection unchecked
            return (ExtensionLoader<S>) extensionLoader;
        }
        //没有则创建
        extensionLoader = new ExtensionLoader<>(type);
        extensionLoaderCache.putIfAbsent(type, extensionLoader);
        //noinspection unchecked
        return (ExtensionLoader<S>) extensionLoader;
    }

    /**
     * 获取默认的扩展类实例，会自动加载 @SimpleRpcSPI 注解中的 value 指定的类实例
     *
     * @return 返回该类的注解 @SimpleRpcSPI.value 指定的类实例
     */
    public T getDefaultExtension() {
        return getExtension(defaultNameCache);
    }

    /**
     * 根据名字获取扩展类实例(单例)
     *
     * @param name 扩展类在配置文件中配置的名字. 如果名字是空的或者空白的，则返回默认扩展
     * @return 单例扩展类实例，如果找不到，则抛出异常
     */
    public T getExtension(String name) {
        if (StrUtil.isBlank(name)) {
            return getDefaultExtension();
        }
        // 从缓存中获取单例
        T extension = extensionsCache.get(name);
        if (extension == null) {
            Object lock = createExtensionLockMap.computeIfAbsent(name, k -> new Object());
            //没有获取到对应实例，则动态创建一个扩展类实例
            synchronized (lock) {
                extension = extensionsCache.get(name);
                if (extension == null) {
                    extension = createExtension(name);
                    extensionsCache.put(name, extension);
                }
            }
        }
        return extension;
    }

    /**
     * 创建对应名字的扩展类实例
     *
     * @param name 扩展名
     * @return 扩展类实例
     */
    private T createExtension(String name) {
        // 获取当前类型所有扩展类
        Map<String, Class<?>> extensionClasses = getAllExtensionClasses();
        // 再根据名字找到对应的扩展类
        Class<?> clazz = extensionClasses.get(name);
        if (clazz == null) {
            throw new IllegalStateException("Extension not found. name=" + name + ", type=" + type.getName());
        }
        try {
            //noinspection unchecked
            return (T) clazz.getConstructor(null).newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Extension not found. name=" + name + ", type=" + type.getName() + ". " + e.toString());
        }
    }

    /**
     * 获取当前类型{@link #type}的所有扩展类
     *
     * @return {name: clazz}
     */
    private Map<String, Class<?>> getAllExtensionClasses() {
        //获取扩展类类型map
        Map<String, Class<?>> extensionClasses = extensionClassesCache.get();
        if (extensionClasses != null) {
            return extensionClasses;
        }
        //没有则创建
        synchronized (extensionClassesCache) {
            extensionClasses = extensionClassesCache.get();
            if (extensionClasses == null) {
                extensionClasses = loadClassesFromResources();
                extensionClassesCache.set(extensionClasses);
            }
        }
        return extensionClasses;
    }

    /**
     * 从资源文件中加载所有扩展类
     * 根据SPI的规则，到资源路径的指定文件中加载对应SPI接口的所有扩展类
     * @return {name: 扩展类}
     */
    private Map<String, Class<?>> loadClassesFromResources() {
        Map<String, Class<?>> extensionClasses = new ConcurrentHashMap<>(8);
        // 扩展配置文件名
        String fileName = EXTENSION_PATH + type.getName();
        // 拿到资源文件夹
        ClassLoader classLoader = ExtensionLoader.class.getClassLoader();
        try {
            Enumeration<URL> resources = classLoader.getResources(fileName);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {
                    // 开始读文件
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        parseLine(line, extensionClasses);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("Parse file fail. " + e.toString());
        }
        return extensionClasses;
    }

    /**
     * 解析行，并且把解析到的类，放到 extensionClasses 中
     *
     * @param line             行
     * @param extensionClasses 扩展类列表
     * @throws ClassNotFoundException 找不到类
     */
    private void parseLine(String line, Map<String, Class<?>> extensionClasses) throws ClassNotFoundException {
        line = line.trim();
        // 忽略#号开头的注释
        if (line.startsWith("#")) {
            return;
        }
        String[] kv = line.split("=");
        if (kv.length != 2 || kv[0].length() == 0 || kv[1].length() == 0) {
            throw new IllegalStateException("Extension file parsing error. Invalid format!");
        }
        // fix：这里如果存在同名的spi实现，将选用最后一个，不做拦截
        if (extensionClasses.containsKey(kv[0])) {
            // throw new IllegalStateException(kv[0] + " is already exists!");
            log.warn("the "+ kv[0] +" spi is already exists!! please warn!");
            return;
        }
        Class<?> clazz = ExtensionLoader.class.getClassLoader().loadClass(kv[1]);
        extensionClasses.put(kv[0], clazz);
    }
}
