package com.atcwl.common.config;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 生产者提供配置
 *
 * @author: WuChengXing
 * @create: 2022-04-22 09:32
 **/
@Data
public class ProviderConfig {

    /**
     * 接口
     */
    protected String interfaceName;

    /**
     * 映射
     */
    protected String beanName;

    /**
     * 别名
     */
    protected String alias;
}
