package com.atcwl.common.config;

import lombok.Data;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 客户端进行远程调用时的一般配置
 *  由于客户端进行代理调用时，代理本身所带信息优先，其他额外信息（如接口名，beanName，别名）由该配置类补齐
 *  因为客户端使用RPC框架时不会去构建Request，Request是框架内部使用的，所以我们需要提供一个个性化外部配置供用户使用
 *  这些配置是必须的，否则没法完成正常的调用
 * @author: WuChengXing
 * @create: 2022-04-22 17:45
 **/
@Data
public class ConsumerConfig {

    /**
     * 接口
     */
    protected String interfaceName;

    /**
     * beanName
     */
    protected String beanName;

    /**
     * 别名
     */
    protected String alias;

    /**
     * 消费者重试次数，未连接成功则抛出异常
     */
    protected Integer tryNum;

    /**
     * 超时时间
     */
    protected Long timeout;
}
