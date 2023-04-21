package com.atcwl.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 项目: simple-rpc
 * <p>
 * 功能描述: 日志公共类
 *
 * @author: WuChengXing
 * @create: 2022-04-29 17:47
 **/
public class FuyouRpcLog {

    private final static Logger logger = LoggerFactory.getLogger(FuyouRpcLog.class);

    public static void info(String msg) {
        logger.info(msg);
    }

    public static void info(String msg, Object var) {
        logger.info(msg, var);
    }

    public static void info(String msg, Object... vars) {
        logger.info(msg, vars);
    }

    public static void error(String msg) {
        logger.error(msg);
    }

    public static void error(String msg, Object var1) {
        logger.error(msg, var1);
    }

    public static void error(String msg, Object... vars) {
        logger.error(msg, vars);
    }

    public static void debug(String msg) {
        logger.debug(msg);
    }

    public static void warn(String msg) {
        logger.warn(msg);
    }

    public static void warn(String msg, Object var1) {
        logger.warn(msg, var1);
    }

    public static void warn(String msg, Object... vars) {
        logger.warn(msg, vars);
    }

}
