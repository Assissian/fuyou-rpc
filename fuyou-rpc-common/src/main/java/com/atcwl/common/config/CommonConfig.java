package com.atcwl.common.config;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 公共配置
 *
 * @author: WuChengXing
 * @create: 2022-05-06 20:05
 **/
@Data
public class CommonConfig {

    private RegistryConfig registryConfig;

    private BaseConfig baseConfig;

    private ConsumerConfig consumerConfig;
}
