package com.atcwl.core.loadbalance.rule.random;

import cn.hutool.core.util.RandomUtil;
import com.atcwl.common.interfaces.impl.LoadBalanceParam;
import com.atcwl.core.loadbalance.AbstractLoadBalance;

import java.util.Map;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 轮询权重算法
 *
 * @author: WuChengXing
 * @create: 2022-05-12 09:47
 **/
public class RandomWeightRule extends AbstractLoadBalance {

    /**
     * 权重:
     * - 127.0.0.1_8881: 20
     * - 127.0.0.1_8882: 30
     * - 127.0.0.1_8883: 50
     * <p>
     * 拉长范围：|0 -- 127.0.0.1_8881 -- 20 | 21 —- 127.0.0.1_8882 -- 50 | 51 -- 127.0.0.1_8883 -- 100|
     *
     * @param urls
     * @return
     */
    @Override
    public String select(Map<String, LoadBalanceParam> urls) {
        int range = 0;
        for (LoadBalanceParam value : urls.values()) {
            range += value.getWeights();
        }
        int index = RandomUtil.randomInt(range);
        for (String url : urls.keySet()) {
            LoadBalanceParam param = urls.get(url);
            Integer weights = param.getWeights();
            if (index < weights) {
                return url;
            }
            index -= weights;
        }
        return "";
    }
}
