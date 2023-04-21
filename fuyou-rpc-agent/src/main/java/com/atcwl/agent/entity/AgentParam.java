package com.atcwl.agent.entity;

import com.atcwl.common.annotation.FuyouRpcConfig;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述: agent参数
 *
 * @author: WuChengXing
 * @create: 2022-06-09 21:04
 **/
@FuyouRpcConfig(prefix = "simple.agent")
public class AgentParam {

    /**
     * 插件类型；jvm;trace;sql
     */
    private String plugins;

    /**
     * 匹配规则选择
     */
    private String containRules;
    private String ignoreRules;

    /**
     * 前缀相关：com.simple.agent;com.test;com.agent
     */
    private String classPrefix;
    private String ignoreClassPrefix;

    /**
     * 类名称匹配：main;hello
     */
    private String classNames;
    private String ignoreClassNames;

    /**
     * 方法名拦截
     */
    private String methodNames;
    private String ignoreMethodNames;

    /**
     * main;hello;toString
     */
    private String interceptMethodRule;
    private String ignoreMethodRule;

    public String getPlugins() {
        return plugins;
    }

    public void setPlugins(String plugins) {
        this.plugins = plugins;
    }

    public String getClassPrefix() {
        return classPrefix;
    }

    public void setClassPrefix(String classPrefix) {
        this.classPrefix = classPrefix;
    }

    public String getIgnoreClassPrefix() {
        return ignoreClassPrefix;
    }

    public void setIgnoreClassPrefix(String ignoreClassPrefix) {
        this.ignoreClassPrefix = ignoreClassPrefix;
    }

    public String getInterceptMethodRule() {
        return interceptMethodRule;
    }

    public void setInterceptMethodRule(String interceptMethodRule) {
        this.interceptMethodRule = interceptMethodRule;
    }

    public String getIgnoreMethodRule() {
        return ignoreMethodRule;
    }

    public void setIgnoreMethodRule(String ignoreMethodRule) {
        this.ignoreMethodRule = ignoreMethodRule;
    }

    public String getClassNames() {
        return classNames;
    }

    public void setClassNames(String classNames) {
        this.classNames = classNames;
    }

    public String getIgnoreClassNames() {
        return ignoreClassNames;
    }

    public void setIgnoreClassNames(String ignoreClassNames) {
        this.ignoreClassNames = ignoreClassNames;
    }

    public String getMethodNames() {
        return methodNames;
    }

    public void setMethodNames(String methodNames) {
        this.methodNames = methodNames;
    }

    public String getIgnoreMethodNames() {
        return ignoreMethodNames;
    }

    public void setIgnoreMethodNames(String ignoreMethodNames) {
        this.ignoreMethodNames = ignoreMethodNames;
    }

    public String getContainRules() {
        return containRules;
    }

    public void setContainRules(String containRules) {
        this.containRules = containRules;
    }

    public String getIgnoreRules() {
        return ignoreRules;
    }

    public void setIgnoreRules(String ignoreRules) {
        this.ignoreRules = ignoreRules;
    }

    @Override
    public String toString() {
        return "AgentParam{" +
                "plugins='" + plugins + '\'' +
                ", containRules='" + containRules + '\'' +
                ", classPrefix='" + classPrefix + '\'' +
                ", ignoreClassPrefix='" + ignoreClassPrefix + '\'' +
                ", classNames='" + classNames + '\'' +
                ", ignoreClassNames='" + ignoreClassNames + '\'' +
                ", methodNames='" + methodNames + '\'' +
                ", ignoreMethodNames='" + ignoreMethodNames + '\'' +
                ", interceptMethodRule='" + interceptMethodRule + '\'' +
                ", ignoreMethodRule='" + ignoreMethodRule + '\'' +
                '}';
    }
}
