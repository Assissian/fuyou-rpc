package com.atcwl.core.loadbalance;


import com.atcwl.common.constrant.enums.LoadBalanceRule;
import com.atcwl.common.interfaces.FuyouRpcLoadBalancer;
import com.atcwl.core.loadbalance.rule.random.RandomRule;
import com.atcwl.core.loadbalance.rule.round.RoundRule;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 负载工厂，与注册中心工厂的作用相似
 *
 * @author: WuChengXing
 * @create: 2022-05-06 17:25
 **/
public class LoadBalanceFactory {
    /**
     * 注意必须选择一个负载均衡算法，否则无法访问服务
     * 所以在该方法返回null时，外部应当处理并报告错误
     * @param rule 负载均衡规则
     * @return 负载均衡实体，负责具体的负载均衡操作
     */
    public static FuyouRpcLoadBalancer create(String rule) {
        if (LoadBalanceRule.RANDOM.getName().equals(rule)) {
            return new RandomRule();
        } else if (LoadBalanceRule.ROUND.getName().equals(rule)) {
            return new RoundRule();
        }
        return null;
    }
}
