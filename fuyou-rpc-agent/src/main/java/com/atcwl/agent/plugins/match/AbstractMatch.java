package com.atcwl.agent.plugins.match;

import net.bytebuddy.matcher.ElementMatcher;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-08-26 12:14
 **/
public abstract class AbstractMatch implements ClazzMatch, MethodMatch {

    @Override
    public ElementMatcher.Junction andJunction() {
        return null;
    }

    @Override
    public ElementMatcher.Junction orJunction() {
        return null;
    }
}
