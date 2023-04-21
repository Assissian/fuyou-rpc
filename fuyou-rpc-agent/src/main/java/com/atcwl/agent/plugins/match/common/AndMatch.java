package com.atcwl.agent.plugins.match.common;

import com.atcwl.agent.plugins.match.AbstractMatch;
import com.atcwl.agent.plugins.match.ClazzMatch;
import net.bytebuddy.matcher.ElementMatcher;

import java.util.List;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-08-26 15:06
 **/
public class AndMatch extends AbstractMatch {

    private final List<ClazzMatch> clazzMatches;

    AndMatch(final List<ClazzMatch> clazzMatches) {
        this.clazzMatches = clazzMatches;
    }

    @Override
    public ElementMatcher.Junction andJunction() {
        ElementMatcher.Junction junction = null;

        for (final ClazzMatch clazzMatch : clazzMatches) {
            if (junction == null) {
                junction = clazzMatch.andJunction();
            } else {
                junction = junction.and(clazzMatch.andJunction());
            }
        }

        return junction;
    }

}
