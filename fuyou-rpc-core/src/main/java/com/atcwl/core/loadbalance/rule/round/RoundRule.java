package com.atcwl.core.loadbalance.rule.round;

import com.atcwl.common.interfaces.impl.LoadBalanceParam;
import com.atcwl.core.loadbalance.AbstractLoadBalance;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 轮询
 *
 * @author: WuChengXing
 * @create: 2022-05-06 17:00
 **/
public class RoundRule extends AbstractLoadBalance {

    private final static LongAdder CUR_INDEX = new LongAdder();

    @Override
    public String select(Map<String, LoadBalanceParam> urls) {
        ArrayList<String> strings = new ArrayList<>(urls.keySet());
        int index = (int) (CUR_INDEX.longValue() % strings.size());
        CUR_INDEX.increment();
        return strings.get(index);
    }
}
