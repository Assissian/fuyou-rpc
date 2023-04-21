package com.atcwl.core.loadbalance.rule.round;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.common.interfaces.impl.LoadBalanceParam;
import com.atcwl.core.loadbalance.AbstractLoadBalance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 普通的加权轮询算法
 *
 * @author: WuChengXing
 * @create: 2022-05-12 16:01
 **/
public class NormalWeightRoundRule extends AbstractLoadBalance {

    private static Map<String, Integer> urlMap = new ConcurrentHashMap<>(4);

    /**
     * 权重:
     * - 127.0.0.1_8881: 20
     * - 127.0.0.1_8882: 30
     * - 127.0.0.1_8883: 50
     * <p>
     * 找到最小公约数：转换成 url ： 20 / 10 = 2 这种数据格式，然后遍历map，在减去对应的次数
     *
     * @param urls
     * @return
     */
    @Override
    public String select(Map<String, LoadBalanceParam> urls) {
        List<Integer> weights = new ArrayList<>(10);
        for (String url : urls.keySet()) {
            weights.add(urls.get(url).getWeights());
        }
        int maxGys = ngcd(weights, weights.size());
        if (CollectionUtil.isEmpty(urlMap)) {
            // 将权重除以最小公约数
            urls.forEach((url, param) -> urlMap.put(url, param.getWeights() / maxGys));
        }
        for (String url : urlMap.keySet()) {
            Integer num = urlMap.get(url);
            if (num > 0) {
                urlMap.put(url, --num);
                return url;
            } else {
                urlMap.remove(url);
                if (CollectionUtil.isEmpty(urlMap)) {
                    // 将权重除以最小公约数
                    urls.forEach((url1, param) -> urlMap.put(url1, param.getWeights() / maxGys));
                    ArrayList<String> urlFirst = new ArrayList<>(urlMap.keySet());
                    Integer numFirst = urlMap.get(urlFirst.get(0));
                    urlMap.put(urlFirst.get(0), --numFirst);
                    return urlFirst.get(0);
                }
            }
        }
        return "";
    }

    public static int gcd(int x, int y) {
        return y == 0 ? x : gcd(y, x % y);
    }

    public static int ngcd(List<Integer> target, int z) {
        if (z == 1) {
            //真正返回的最大公约数
            return target.get(0);
        }
        //递归调用，两个数两个数的求
        return gcd(target.get(z - 1), ngcd(target, z - 1));
    }

    public static void main(String[] args) {
        Map<String, LoadBalanceParam> urls = new ConcurrentHashMap<>(4);
        LoadBalanceParam param1 = new LoadBalanceParam();
        param1.setWeights(70);
        LoadBalanceParam param2 = new LoadBalanceParam();
        param2.setWeights(20);
        LoadBalanceParam param3 = new LoadBalanceParam();
        param3.setWeights(30);
        urls.put("127.0.0.1_8081", param1);
        urls.put("127.0.0.1_8082", param2);
        urls.put("127.0.0.1_8083", param3);
        for (int i = 0; i < 20; i++) {
            NormalWeightRoundRule rule = new NormalWeightRoundRule();
            System.out.println(rule.select(urls));
        }
    }
}
