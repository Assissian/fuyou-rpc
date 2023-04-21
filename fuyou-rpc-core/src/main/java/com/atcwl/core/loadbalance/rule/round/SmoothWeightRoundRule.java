package com.atcwl.core.loadbalance.rule.round;


import com.atcwl.common.interfaces.impl.LoadBalanceParam;
import com.atcwl.common.interfaces.impl.ServiceWeight;
import com.atcwl.core.loadbalance.AbstractLoadBalance;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 平滑加权轮询
 *
 * @author: WuChengXing
 * @create: 2022-05-12 16:00
 **/
public class SmoothWeightRoundRule extends AbstractLoadBalance {

    private static Map<String, ServiceWeight> weightPathMap = new ConcurrentHashMap<>(4);

    /**
     * 权重:
     * - 127.0.0.1_8881: 3
     * - 127.0.0.1_8882: 2
     * - 127.0.0.1_8883: 1
     * <p>
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜ requestNum ｜ currentWeight ｜ currentMax ｜      url       ｜ max(currentWeight)-totalWeight ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     1      ｜     3,2,1     ｜     3      ｜ 127.0.0.1_8881 ｜             -3,2,1             ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     2      ｜     0,4,2     ｜     4      ｜ 127.0.0.1_8882 ｜             0,-2,2             ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     3      ｜     3,0,3     ｜     3      ｜ 127.0.0.1_8881 ｜             -3,0,3             ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     4      ｜     0,2,4     ｜     4      ｜ 127.0.0.1_8883 ｜              0,2,-2            ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     5      ｜     3,4,-1    ｜     4      ｜ 127.0.0.1_8882 ｜             3,-2,-1            ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * ｜     6      ｜     6,0,0     ｜     6      ｜ 127.0.0.1_8881 ｜              0,0,0             ｜
     * ｜------------｜---------------｜------------｜----------------｜--------------------------------｜
     * <p>
     * - 每次从当前权重中选出最大权重代表的服务器作为返回结果
     * - 选择完具体服务器后，把当前最大权重减去权重总和，再把所有权重都跟初始权重(3,2,1)相加
     * - 3,2,1，选出3代表的127.0.0.1_8881服务器，然后减去6成为 -3,2,1，再加上3,2,1，成为0,2,1，下次的当前权重即为0,2,1
     *
     * @param urls
     * @return
     */
    @Override
    public String select(Map<String, LoadBalanceParam> urls) {
        return getServerPath(urls);
    }

    /**
     * 每个url对应一个权重
     *
     * @param urls
     * @return
     */
    public static String getServerPath(Map<String, LoadBalanceParam> urls) {
        // 拿到总权重
        int range = 0;
        for (LoadBalanceParam value : urls.values()) {
            range += value.getWeights();
        }

        // 动态权重Map为空，那么初始化端口权重map
        if (weightPathMap.isEmpty()) {
            urls.forEach((url, param) -> weightPathMap.put(url, new ServiceWeight(url, param.getWeights(), 0)));
        }
        // 当前权重 + 初始权重 = 下一轮的权重
        for (ServiceWeight serviceWeight : weightPathMap.values()) {
            serviceWeight.curWeight += serviceWeight.weight;
        }

        // 将最大的权重赋值
        ServiceWeight maxCurWeight = null;
        for (ServiceWeight weight : weightPathMap.values()) {
            // 找最大的权重值
            if (maxCurWeight == null || weight.curWeight > maxCurWeight.curWeight) {
                maxCurWeight = weight;
            }
        }
        // 减去总权重
        assert maxCurWeight != null;
        maxCurWeight.curWeight -= range;
        // 拿到端口
        return maxCurWeight.url;
    }

    public static void main(String[] args) {
        Map<String, LoadBalanceParam> urls = new ConcurrentHashMap<>(4);
        LoadBalanceParam param1 = new LoadBalanceParam();
        param1.setWeights(3);
        LoadBalanceParam param2 = new LoadBalanceParam();
        param2.setWeights(2);
        LoadBalanceParam param3 = new LoadBalanceParam();
        param3.setWeights(1);
        urls.put("127.0.0.1_8081", param1);
        urls.put("127.0.0.1_8082", param2);
        urls.put("127.0.0.1_8083", param3);
        for (int i = 0; i < 7; i++) {
            SmoothWeightRoundRule rule = new SmoothWeightRoundRule();
            System.out.println(rule.select(urls));
        }
    }
}
