package com.atcwl.common.constrant.enums;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 健康状态
 *
 * @author: WuChengXing
 * @create: 2022-06-19 11:26
 **/
public enum HealthStatus {

    /**
     *
     */
    IS_HEALTH("1", "isHealth"),
    NOT_HEALTH("2", "notHealth"),
    ;

    private final String code;
    private final String desc;

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    HealthStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
