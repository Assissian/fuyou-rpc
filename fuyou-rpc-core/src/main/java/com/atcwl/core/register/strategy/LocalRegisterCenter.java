package com.atcwl.core.register.strategy;

import com.alibaba.fastjson2.JSON;
import com.atcwl.common.config.SimpleRpcUrl;
import com.atcwl.common.interfaces.impl.RegisterInfo;
import com.atcwl.core.register.AbstractRegisterCenter;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 本地注册中心
 *
 * @author: WuChengXing
 * @create: 2022-05-04 00:34
 **/
public class LocalRegisterCenter extends AbstractRegisterCenter {

    private static Map<String, String> SERVICE_CACHE;

    @Override
    public void init(SimpleRpcUrl url) {
        SERVICE_CACHE = new ConcurrentHashMap<>(16);
    }

    @Override
    public String register(RegisterInfo request) {
        SERVICE_CACHE.put(request.getInterfaceName() + "_" + request.getAlias(), JSON.toJSONString(request));
        return "";
    }

    @Override
    public String get(RegisterInfo request) {
        return SERVICE_CACHE.get(request.getInterfaceName() + "_" + request.getAlias());
    }

    @Override
    protected Boolean buildDataAndSave(String key, String hostPort, String request) {
        return null;
    }

    @Override
    protected Boolean buildAppName(String appName, String rpcService) {
        return null;
    }

    @Override
    protected Map<String, String> getLoadBalanceData(String key) {
        return null;
    }

    @Override
    protected List<String> getMultiKeyValue(List<String> keys, String machine) {
        return null;
    }
}
