package com.atcwl.agent.plugins.impl.rule;

import com.atcwl.agent.entity.AgentParam;
import com.atcwl.agent.util.AgentParamUtil;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-10 00:15
 **/
public class DefaultRules {

    public static ElementMatcher.Junction<TypeDescription> defaultIgnoreClass() {
        return ElementMatchers.not(ElementMatchers.nameStartsWith("org.slf4j")
                .or(ElementMatchers.nameStartsWith("com.simple.classbytecode.bytebuddy.common"))
                .or(ElementMatchers.nameStartsWith("net.buddy"))
                .or(ElementMatchers.nameStartsWith("org.springframework"))
                .or(ElementMatchers.nameStartsWith("com.simple.rpc.springboot"))
                .or(ElementMatchers.nameStartsWith("com.simple.rpc.core"))
                .or(ElementMatchers.nameStartsWith("io.netty"))
                .or(ElementMatchers.nameStartsWith("java.lang"))
                .or(ElementMatchers.nameStartsWith("org.apache"))
        );
    }

    public static ElementMatcher.Junction<MethodDescription> defaultIgnoreMethod() {
        return ElementMatchers.not(ElementMatchers.named("main")
                .or(ElementMatchers.nameStartsWith("hashCode"))
                .or(ElementMatchers.nameStartsWith("toString"))
                .or(ElementMatchers.nameStartsWith("setBeanFactory"))
                .or(ElementMatchers.nameStartsWith("helloWorld"))
        );
    }

    public static ElementMatcher.Junction<TypeDescription> ignoreClassParam(AgentParam param) {
        List<String> strings = AgentParamUtil.dealParam(param.getIgnoreClassPrefix());
        return ElementMatchers.not(ElementMatchers.anyOf(strings));
    }

    public static ElementMatcher.Junction<TypeDescription> containClassParam(AgentParam param) {
        List<String> strings = AgentParamUtil.dealParam(param.getClassPrefix());
        List<ElementMatcher.Junction<TypeDescription>> values = new ArrayList<>();
        strings.forEach(s -> {
            values.add(ElementMatchers.nameStartsWith(s));
        });
        return ElementMatchers.anyOf(values);
    }

    public static ElementMatcher.Junction<MethodDescription> ignoreMethodParam(AgentParam param) {
        List<String> strings = AgentParamUtil.dealParam(param.getInterceptMethodRule());
        return ElementMatchers.not(ElementMatchers.anyOf(strings));
    }

    public static ElementMatcher.Junction<MethodDescription> containMethodParam(AgentParam param) {
        List<String> strings = AgentParamUtil.dealParam(param.getInterceptMethodRule());
        return ElementMatchers.anyOf(strings);
    }
}
