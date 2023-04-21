package com.atcwl.core.register;

import com.atcwl.common.interfaces.RegisterCenter;
import com.atcwl.common.spi.ExtensionLoader;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册中心工厂
 *
 * @author: WuChengXing
 * @create: 2022-04-22 22:09
 **/
public class RegisterCenterFactory {
    /**
     * 根据注册中心类型创建对应的注册中心对象RegisterCenter
     * @param registerType
     * @return
     */
    public static RegisterCenter create(String registerType) {
        return getRegisterCenter(registerType);
    }

    /**
     * 注册中心对象也属于可扩展接口，因此同样采用了SPI接口的实现方式
     * @param registerType
     * @return
     */
    private static RegisterCenter getRegisterCenter(String registerType) {
        return ExtensionLoader.getLoader(RegisterCenter.class).getExtension(registerType);
    }
}
