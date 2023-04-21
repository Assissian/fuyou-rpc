package com.atcwl.common.annotation;

import java.lang.annotation.*;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 服务注入
 *
 * @author: WuChengXing
 * @create: 2022-05-04 16:27
 **/
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuyouRpcReference {

    /**
     * 版本，没有特殊要求不用填写
     *
     * @return 版本，默认""
     */
    String version() default "";

    /**
     * 别名
     *
     * @return
     */
    String alias() default "";

    /**
     * 是否开启接口负载均衡，针对于单个接口
     *
     * @return
     */
    boolean loadBalance() default true;

    /**
     * 负载规则
     *
     * @return
     */
    String rule() default "random";
}
