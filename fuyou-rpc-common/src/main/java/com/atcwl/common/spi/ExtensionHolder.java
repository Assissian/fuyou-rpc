package com.atcwl.common.spi;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 缓存获取类, 用于单个变量既可以做锁又可以做值
 * 主要用于缓存SPI接口和对应实现类的关系
 * @author: WuChengXing
 * @create: 2022-05-07 15:47
 **/
public class ExtensionHolder<T> {

    private T value;

    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}
