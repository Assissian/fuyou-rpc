package com.atcwl.common.exception.network;


import com.atcwl.common.exception.SimpleRpcBaseException;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: netty初始化异常
 *
 * @author: WuChengXing
 * @create: 2022-04-25 00:17
 **/
public class NettyInitException extends SimpleRpcBaseException {

    public NettyInitException(String message) {
        super(message);
    }
}
