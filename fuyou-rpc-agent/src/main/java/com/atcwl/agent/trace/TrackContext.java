package com.atcwl.agent.trace;

/**
 * 项目: class-byte-code
 * <p>
 * 功能描述:
 * 链路追踪上下文
 * @author: WuChengXing
 * @create: 2022-06-03 00:47
 **/
public class TrackContext {
    //每当进入一个新的区段Span，就会重新设置trackLocal，放入当前追踪的链路的traceId
    //这样可以构建新的区段Span对象，用于记录新的链路信息
    private static final ThreadLocal<String> trackLocal = new ThreadLocal<>();
    //所以一旦有某个Span将traceId取走之后，就应该将其从ThreadLocal中移除，等到进入新的Span之前在重新设置
    public static void clear(){
        trackLocal.remove();
    }

    public static String getTraceId(){
        return trackLocal.get();
    }

    public static void setTraceId(String traceId){
        trackLocal.set(traceId);
    }
}
