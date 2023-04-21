package com.atcwl.common.annotation.filter;


import com.atcwl.common.constrant.FilterConstant;

import java.lang.annotation.*;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-05 00:27
 **/
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Filter {

    String name();

    String type() default FilterConstant.ALL;

}
