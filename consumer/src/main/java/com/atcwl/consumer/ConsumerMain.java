package com.atcwl.consumer;

import com.alibaba.fastjson2.JSON;
import com.atcwl.common.config.ConfigManager;
import com.atcwl.common.config.RegistryConfig;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.exception.network.NettyInitException;
import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.core.net.client.FuyouRpcClient;
import com.atcwl.core.net.message.Request;
import com.atcwl.core.reflect.RpcProxy;
import com.atcwl.core.register.RegisterCenterFactory;
import io.netty.channel.ChannelFuture;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
public class ConsumerMain {
    static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ClassNotFoundException {
        // 初始化注册中心
        RegistryConfig registryConfig = ConfigManager.getInstant().getRegistryConfig();
        SimpleRpcUrl simpleRpcUrl = SimpleRpcUrl.toSimpleRpcUrl(registryConfig);
        RegisterCenter registerCenter = RegisterCenterFactory.create(simpleRpcUrl.getType());
        registerCenter.init(simpleRpcUrl);

        Request request = new Request();
        String interfaceName = "com.atcwl.api.common.service.CoreHelloService";
        request.setInterfaceName(interfaceName);
        request.setAlias("rpcProvider");
        request.setRequestId(1001L);
        String infoStr = registerCenter.get(Request.request2Register(request));
        request = JSON.parseObject(infoStr, Request.class);
        ChannelFuture channelFuture = null;
        System.out.println("服务端的地址和端口：" +  request.getHost() + "-" + request.getPort());
        //获取通信channel
        if (null == channelFuture) {
            FuyouRpcClient clientSocket = new FuyouRpcClient(request);
            executorService.submit(clientSocket);
            for (int i = 0; i < 100; i++) {
                if (null != channelFuture) {
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channelFuture = clientSocket.getChannelFuture();
            }
        }
        if (null == channelFuture) {
            throw new NettyInitException("客户端未连接上服务端，考虑增加重试次数");
        }
        request.setChannel(channelFuture.channel());
        Object invoke = RpcProxy.getServiceImpl(Class.forName(interfaceName), null);
        System.out.println(invoke);
    }
}
