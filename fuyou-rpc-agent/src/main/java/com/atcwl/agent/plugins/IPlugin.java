package com.atcwl.agent.plugins;

import java.util.List;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-03 00:46
 **/
public interface IPlugin {

    /**
     * 名称
     *
     * @return
     */
    String name();

    /**
     * 监控点
     *
     * @return
     */
    List<InterceptPoint> buildInterceptPoint();

    /**
     * 拦截器类
     *
     * @return
     */
    Class adviceClass();
}
