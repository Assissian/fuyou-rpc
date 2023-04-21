package com.atcwl.common.exception;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 基础的异常信息
 *
 * @author: WuChengXing
 * @create: 2022-04-24 00:42
 **/
public class SimpleRpcBaseException extends RuntimeException {

    protected String message;

    public SimpleRpcBaseException(String message) {
        super(message);
    }

    public SimpleRpcBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public SimpleRpcBaseException(Throwable cause) {
        super(cause);
    }

    public SimpleRpcBaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
