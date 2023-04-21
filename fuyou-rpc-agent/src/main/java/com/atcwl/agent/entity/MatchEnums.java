package com.atcwl.agent.entity;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 匹配枚举
 *
 * @author: WuChengXing
 * @create: 2022-08-26 14:32
 **/
public enum MatchEnums {

    /**
     *
     */
    PREFIX("01", "prefix"),
    NAMED("02", "named"),
    IGNORE_PREFIX("03", "ignore prefix"),
    IGNORE_NAMED("04", "ignore named"),
    ;

    private final String code;
    private final String desc;

    MatchEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
