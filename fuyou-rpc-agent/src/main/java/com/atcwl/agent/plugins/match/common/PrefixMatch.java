package com.atcwl.agent.plugins.match.common;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.agent.plugins.match.AbstractMatch;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-08-26 14:03
 **/
public class PrefixMatch extends AbstractMatch {

    private final List<String> prefixes;

    private PrefixMatch(List<String> prefixes) {
        if (CollectionUtil.isEmpty(prefixes)) {
            throw new IllegalArgumentException("prefixes argument is null or empty");
        }
        this.prefixes = prefixes;
    }

    @Override
    public ElementMatcher.Junction andJunction() {
        ElementMatcher.Junction junction = null;

        for (String prefix : prefixes) {
            if (junction == null) {
                junction = ElementMatchers.nameStartsWith(prefix);
            } else {
                junction = junction.and(ElementMatchers.nameStartsWith(prefix));
            }
        }

        return junction;
    }

    @Override
    public ElementMatcher.Junction orJunction() {
        ElementMatcher.Junction junction = null;

        for (String prefix : prefixes) {
            if (junction == null) {
                junction = ElementMatchers.nameStartsWith(prefix);
            } else {
                junction = junction.or(ElementMatchers.nameStartsWith(prefix));
            }
        }

        return junction;
    }

    public static PrefixMatch nameStartsWith(final List<String> prefixes) {
        return new PrefixMatch(prefixes);
    }

}
