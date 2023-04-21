package com.atcwl.common.annotation;

import com.atcwl.common.constrant.CommonConstant;

import java.lang.annotation.*;

/**
 * @Author cwl
 * @date
 * @apiNote
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface FuyouRpcSPI {
    /**
     * 默认扩展类全路径
     *
     * @return 默认不填是 default
     */
    String value() default CommonConstant.DEFAULT;
}
