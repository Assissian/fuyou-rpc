package com.atcwl.common.annotation;

import java.lang.annotation.*;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: config配置注解，放在配置类上面，可以使用loader进行加载
 *
 * @author: WuChengXing
 * @create: 2022-04-30 10:40
 **/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuyouRpcConfig {

    /**
     * 前缀名
     *
     * @return 前缀，不能空
     */
    String prefix();
}
