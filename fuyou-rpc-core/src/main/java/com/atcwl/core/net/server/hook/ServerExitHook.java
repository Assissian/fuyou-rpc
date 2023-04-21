package com.atcwl.core.net.server.hook;


import com.atcwl.common.cache.RegisterInfoCache;
import com.atcwl.common.cache.SimpleRpcServiceCache;
import com.atcwl.common.config.LocalAddressInfo;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.common.network.HookEntity;
import com.atcwl.core.register.RegisterCenterFactory;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: server端的退出
 *
 * @author: WuChengXing
 * @create: 2022-05-10 11:28
 **/
public class ServerExitHook {
    /**
     * 该方法由服务端退出时自行执行
     */
    public static void addShutdownHook() {
        //SimpleRpcLog.info("addShutdownHook for clearAll");
        //给运行时环境挂在一个关闭钩子函数，在服务退出时自行取消注册
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            String url = LocalAddressInfo.LOCAL_HOST + "_" + LocalAddressInfo.PORT;
            //根据本服务配置拿到对应注册中心对象
            RegisterCenter registerCenter = RegisterCenterFactory.create(SimpleRpcUrl.toSimpleRpcUrl(RegisterInfoCache.getRegisterInfo(url)).getType());
            //拿到本服务提供的所有服务接口名称
            List<String> strings = SimpleRpcServiceCache.allKeys();
            HookEntity hookEntity = new HookEntity();
            hookEntity.setRpcServiceNames(strings);
            hookEntity.setServerUrl(LocalAddressInfo.LOCAL_HOST);
            hookEntity.setServerPort(LocalAddressInfo.PORT);
            //解绑，会将所有该服务提供的接口都解绑
            registerCenter.unregister(hookEntity);
            //SimpleRpcLog.warn("退出服务");
        }));
    }
}
