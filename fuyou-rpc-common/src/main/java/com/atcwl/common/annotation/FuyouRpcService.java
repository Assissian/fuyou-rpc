package com.atcwl.common.annotation;

import java.lang.annotation.*;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: rpc对应的提供类
 *
 * @author: WuChengXing
 * @create: 2022-05-04 16:22
 **/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuyouRpcService {

    String alias() default "";
}
