package com.atcwl.common.config;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-04-22 17:45
 **/
@Data
public class ServerConfig {

    /**
     * 注册地址
     */
    protected String address;

    protected String username;

    protected String password;

    protected String database;

    protected String table;

    /**
     * 超时时间
     */
    protected Long timeout;

    /**
     * 重试次数
     */
    protected Integer tryNum;
}
