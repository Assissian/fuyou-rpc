package com.atcwl.provider;

import com.atcwl.common.cache.SimpleRpcServiceCache;
import com.atcwl.common.config.ConfigManager;
import com.atcwl.common.config.LocalAddressInfo;
import com.atcwl.common.config.RegistryConfig;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.net.server.FuyouRpcServer;
import com.atcwl.core.register.RegisterCenterFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class ProviderMain {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws InterruptedException {
        // 初始化注册中心
        RegistryConfig registryConfig = ConfigManager.getInstant().getRegistryConfig();
        SimpleRpcUrl simpleRpcUrl = SimpleRpcUrl.toSimpleRpcUrl(registryConfig);
        RegisterCenter registerCenter = RegisterCenterFactory.create(simpleRpcUrl.getType());
        registerCenter.init(simpleRpcUrl);
        Request request = new Request();
        request.setRequestId(1001L);

        String interfaceName = "com.atcwl.api.common.service.CoreHelloService";
        request.setInterfaceName(interfaceName);
        request.setAlias("rpcProvider");
        // 初始化
        FuyouRpcServer rpcServerSocket = new FuyouRpcServer(new Request());
        executorService.submit(rpcServerSocket);
        while (!rpcServerSocket.isActiveServer()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {
            }
        }
        request.setHost(LocalAddressInfo.LOCAL_HOST);
        request.setPort(LocalAddressInfo.PORT);
        System.out.println("服务端的地址和端口：" +  request.getHost() + "-" + request.getPort());
        registerCenter.register(Request.request2Register(request));
        SimpleRpcServiceCache.addService("rpcProvider", new CoreHelloServiceImpl());
    }
}
