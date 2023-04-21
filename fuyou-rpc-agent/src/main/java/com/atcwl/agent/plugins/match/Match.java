package com.atcwl.agent.plugins.match;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 顶级匹配类
 *
 * @author: WuChengXing
 * @create: 2022-08-26 13:40
 **/
public interface Match {

    /**
     * 构建匹配规则
     *
     * @return
     */
    ElementMatcher.Junction andJunction();

    /**
     * 构建忽略匹配规则
     *
     * @return
     */
    ElementMatcher.Junction orJunction();
}
