package com.atcwl.common.constrant.enums;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 注册中心枚举
 *
 * @author: WuChengXing
 * @create: 2022-04-22 22:13
 **/
public enum RegisterEnum {
    /**
     * 注册中心类型
     */
    LOCAL("0", "local"),
    REDIS("1", "redis"),
    MYSQL("2", "mysql"),
    ORACLE("3", "oracle"),
    ZOOKEEPER("4", "zookeeper"),
    NACOS("5", "nacos"),
    ;

    RegisterEnum(String code, String registerType) {
        this.code = code;
        this.registerType = registerType;
    }

    private final String code;
    private final String registerType;

    public String getCode() {
        return code;
    }

    public String getRegisterType() {
        return registerType;
    }
}
