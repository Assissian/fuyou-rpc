package com.atcwl.common.annotation.filter;

import java.lang.annotation.*;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 调用之前的过滤注解
 *
 * @author: WuChengXing
 * @create: 2022-06-05 00:25
 **/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface InvokeBeforeFilter {

    String name();
}
