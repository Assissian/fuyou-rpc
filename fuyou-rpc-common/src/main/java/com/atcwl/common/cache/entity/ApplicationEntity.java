package com.atcwl.common.cache.entity;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 应用级别的参数
 *
 * @author: WuChengXing
 * @create: 2022-06-18 13:40
 **/
@Data
public class ApplicationEntity {

    private String applicationName;

    private String host;

    private Integer port;
}
