package com.atcwl.agent.plugins.match.common;

import cn.hutool.core.collection.CollectionUtil;
import com.atcwl.agent.plugins.match.AbstractMatch;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-08-26 14:18
 **/
public class MultiNamedMatch extends AbstractMatch {

    private final List<String> matchClassNames;

    private MultiNamedMatch(List<String> classNames) {
        if (CollectionUtil.isEmpty(classNames)) {
            throw new IllegalArgumentException("match class names is null");
        }
        this.matchClassNames = classNames;
    }

    @Override
    public ElementMatcher.Junction andJunction() {
        ElementMatcher.Junction junction = null;
        for (String name : matchClassNames) {
            if (junction == null) {
                junction = named(name);
            } else {
                junction = junction.and(named(name));
            }
        }
        return junction;
    }

    @Override
    public ElementMatcher.Junction orJunction() {
        ElementMatcher.Junction junction = null;
        for (String name : matchClassNames) {
            if (junction == null) {
                junction = named(name);
            } else {
                junction = junction.or(named(name));
            }
        }
        return junction;
    }

    public static MultiNamedMatch byMultiClassMatch(List<String> classNames) {
        return new MultiNamedMatch(classNames);
    }

}
