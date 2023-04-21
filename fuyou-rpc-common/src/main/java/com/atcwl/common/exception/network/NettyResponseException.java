package com.atcwl.common.exception.network;

import com.atcwl.common.exception.SimpleRpcBaseException;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: netty的handler里面捕获的异常
 *
 * @author: WuChengXing
 * @create: 2022-04-25 00:14
 **/
public class NettyResponseException extends SimpleRpcBaseException {
    public NettyResponseException(String message) {
        super(message);
    }

    public NettyResponseException(Throwable cause) {
        super(cause);
    }
}
