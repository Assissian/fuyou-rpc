package com.atcwl.agent.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.atcwl.agent.constant.AgentConstant;
import com.atcwl.agent.entity.AgentParam;
import com.atcwl.common.config.ConfigManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述: 参数处理工具
 *
 * @author: WuChengXing
 * @create: 2022-06-09 23:56
 **/
public class AgentParamUtil {

    /**
     * 处理参数以;组成的字符串
     *
     * @param param
     * @return
     */
    public static List<String> dealParam(String param) {
        if (Objects.isNull(param) || "".equals(param)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(param.split(";")));
    }

    public static AgentParam dealAgentParam(String agentArgs) {
        AgentParam agentParam = new AgentParam();
        try {
            // 配置在启动时，后面的参数优先级最高
            if (!StrUtil.isBlank(agentArgs)) {
                agentParam = JSON.parseObject(agentArgs, AgentParam.class);
                if (StrUtil.isBlank(agentParam.getPlugins())) {
                    agentParam.setPlugins(AgentConstant.PLUGIN_TRACE);
                }
                return agentParam;
            } else {
                AgentParam agentConfig = ConfigManager.getInstant().loadConfig(AgentParam.class);
                if (!Objects.isNull(agentConfig) && !StrUtil.isBlank(agentConfig.getPlugins())) {
                    return agentConfig;
                } else {
                    agentParam.setPlugins(AgentConstant.PLUGIN_TRACE);
                    return agentParam;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            agentParam.setPlugins(AgentConstant.PLUGIN_TRACE);
            return agentParam;
        }

    }

    public static void main(String[] args) {
        AgentParam agentParam = new AgentParam();
        agentParam.setPlugins("trace");
        agentParam.setContainRules("01");
        agentParam.setIgnoreRules("04");
        agentParam.setClassPrefix("com.simple.rpc");
        agentParam.setIgnoreClassNames("p1");
        System.out.println(JSON.toJSONString(agentParam));
    }
}
