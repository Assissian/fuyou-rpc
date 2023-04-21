package com.atcwl.common.exception.network;


import com.atcwl.common.exception.SimpleRpcBaseException;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述:
 *
 * @author: WuChengXing
 * @create: 2022-06-04 18:39
 **/
public class NettyInvokeException extends SimpleRpcBaseException {

    public NettyInvokeException(String message) {
        super(message);
    }
}
