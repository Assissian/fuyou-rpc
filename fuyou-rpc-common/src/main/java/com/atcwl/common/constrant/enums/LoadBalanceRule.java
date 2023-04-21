package com.atcwl.common.constrant.enums;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 负载均衡算法
 *
 * @author: WuChengXing
 * @create: 2022-05-06 16:27
 **/
public enum LoadBalanceRule {

    /**
     *
     */
    RANDOM(1, "random"),
    ROUND(2, "round");

    private final Integer value;
    private final String name;

    /**
     * 通过值获取压缩类型枚举
     *
     * @param value 值
     * @return 如果获取不到，返回 DUMMY
     */
    public static LoadBalanceRule fromValue(Integer value) {
        for (LoadBalanceRule loadBalanceRule : LoadBalanceRule.values()) {
            if (loadBalanceRule.getValue().equals(value)) {
                return loadBalanceRule;
            }
        }
        return RANDOM;
    }

    /**
     * 通过名字获取压缩类型枚举
     *
     * @param name 名字
     * @return 如果获取不到，返回 DUMMY
     */
    public static LoadBalanceRule fromName(String name) {
        for (LoadBalanceRule loadBalanceRule : LoadBalanceRule.values()) {
            if (loadBalanceRule.getName().equals(name)) {
                return loadBalanceRule;
            }
        }
        return RANDOM;
    }

    public Integer getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    LoadBalanceRule(Integer value, String name) {
        this.value = value;
        this.name = name;
    }
}
