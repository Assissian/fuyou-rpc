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
 * @create: 2022-08-26 15:21
 **/
public class OrMatch extends AbstractMatch {

    private List<ClazzMatch> clazzMatches;

    OrMatch(final List<ClazzMatch> clazzMatches) {
        this.clazzMatches = clazzMatches;
    }

    @Override
    public ElementMatcher.Junction orJunction() {
        ElementMatcher.Junction junction = null;

        for (final ClazzMatch clazzMatch : clazzMatches) {
            if (junction == null) {
                junction = clazzMatch.orJunction();
            } else {
                junction = junction.or(clazzMatch.orJunction());
            }
        }
        return junction;
    }

    public static OrMatch or(List<ClazzMatch> clazzMatches) {
        return new OrMatch(clazzMatches);
    }

}
