package com.atcwl.common.network;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 公共线程传递类
 *
 * @author: WuChengXing
 * @create: 2022-05-31 16:50
 **/
public class CommonThreadLocal {

    public static ThreadLocal get() {
        return new ThreadLocal();
    }

}
