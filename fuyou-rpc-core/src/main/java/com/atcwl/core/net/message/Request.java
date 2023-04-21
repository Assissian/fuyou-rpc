package com.atcwl.core.net.message;

import com.atcwl.common.interfaces.impl.FuyouRpcContext;
import com.atcwl.common.interfaces.impl.RegisterInfo;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * 自定义请求类型：协议是自定的，因此请求类型定制会更加符合
 * 本RPC是直接基于传输层的，并没有依赖HTTP，因此最好自定义请求格式
 * @Author cwl
 * @date
 * @apiNote
 */
public class Request {
    /**
     * 保存此次请求处理用到的channel对象，可以保存相应连接，从而达到复用连接的效果
     */
    private transient Channel channel;
    private transient ChannelFuture channelFuture;
    private FuyouRpcContext fuyouRpcContext;
    /**
     * 请求ID：唯一标识请求，这里通过AtomicLong生成
     */
    private Long requestId;
    /**
     * 应用名称：考虑到服务端存在集群，那么对于同一种服务最好使用特定服务名标识
     * 注册中心需要保存对应服务名与对应服务集群URL的对应关系
     */
    private String applicationName;
    /**
     * 接口名称：指明想要远程调用的接口
     */
    private String interfaceName;
    /**
     * 目标接口实现类所对应的beanName，结合Spring的时候会用到
     */
    private String beanName;
    /**
     * 别名，查找本地注册表时会用到
     */
    private String alias;
    /**
     * 方法名称：指定远程调用方法名称
     */
    private String methodName;
    /**
     * 远程调用接口的参数类型
     */
    private Class[] parameterTypes;
    /**
     * 接口所需参数
     */
    private Object[] parameters;
    /**
     * 目的主机IP地址
     */
    private String host;
    /**
     *目的主机端口
     */
    private Integer port;
    /**
     * 超时时间，这里的超时时间应该是访问服务时允许等待服务结果的最长时间
     */
    private Long timeout;
    /**
     * 负载均衡算法指定
     */
    private String loadBalanceRule;
    /**
     * 指定心跳间隔时间
     * 由于我们没有使用HTTP协议，而是完全自定的请求
     * 所以如果想要使用TCP保活机制，我们需要在自己的请求内携带心跳间隔时间，以便进行保活
     * HTTP本身是有相应的头信息来标识心跳间隔时间的
     */
    private Long beatIntervalTime;
    /**
     * 我们采用了TCP长连接来减少连接建立的次数
     * 但是也不能让连接一直保持，所以我们给定了一个连接最大空闲时间，超过该空闲时间就断开连接
     * 跟HTTP类似
     */
    private Long stopConnectTime;
    /**
     * 指定此次请求的序列化方式
     */
    private String serializer;
    /**
     * 指定此次请求的压缩方式
     */
    private String compressor;
    /**
     * 指定此次请求的注册中心类型
     */
    private String register;
    /**
     * 此次服务请求的最大重试次数
     */
    private Integer retryNum;
    /**
     * 负载均衡的权重
     */
    private Integer weights;
    /**
     * 判断服务hfs的健康状态
     */
    private String health;

    /**
     *将注册信息转化为请求信息
     * @param info
     * @return
     */
    public static Request register2Request(RegisterInfo info) {
        Request request = new Request();
        request.setHost(info.getHost());
        request.setPort(info.getPort());
        request.setInterfaceName(info.getInterfaceName());
        request.setTimeout(info.getTimeout());
        request.setRetryNum(info.getRetryNum());
        request.setBeanName(info.getBeanName());
        request.setAlias(info.getAlias());
        request.setLoadBalanceRule(info.getLoadBalanceRule());
        request.setSerializer(info.getSerializer());
        request.setCompressor(info.getCompressor());
        request.setRegister(info.getRegister());
        request.setWeights(info.getWeights());
        request.setApplicationName(info.getApplicationName());
        request.setHealth(info.getHealth());
        return request;
    }

    /**
     * 将请求转化为注册信息
     * @param request
     * @return
     */
    public static RegisterInfo request2Register(Request request) {
        RegisterInfo registerInfo = new RegisterInfo();
        registerInfo.setInterfaceName(request.getInterfaceName());
        registerInfo.setBeanName(request.getBeanName());
        registerInfo.setAlias(request.getAlias());
        registerInfo.setTimeout(request.getTimeout());
        registerInfo.setRetryNum(request.getRetryNum());
        registerInfo.setHost(request.getHost());
        registerInfo.setPort(request.getPort());
        registerInfo.setLoadBalanceRule(request.getLoadBalanceRule());
        registerInfo.setWeights(request.getWeights());
        registerInfo.setSerializer(request.getSerializer());
        registerInfo.setCompressor(request.getCompressor());
        registerInfo.setRegister(request.getRegister());
        registerInfo.setApplicationName(request.getApplicationName());
        registerInfo.setHealth(request.getHealth());
        return registerInfo;
    }


    public Request() {
    }

    public ChannelFuture getChannelFuture() {
        return channelFuture;
    }

    public void setChannelFuture(ChannelFuture channelFuture) {
        this.channelFuture = channelFuture;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public FuyouRpcContext getFuyouRpcContext() {
        return fuyouRpcContext;
    }

    public void setFuyouRpcContext(FuyouRpcContext fuyouRpcContext) {
        this.fuyouRpcContext = fuyouRpcContext;
    }

    public long getRequestId() {
        return requestId;
    }

    public void setRequestId(long requestId) {
        this.requestId = requestId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getLoadBalanceRule() {
        return loadBalanceRule;
    }

    public void setLoadBalanceRule(String loadBalanceRule) {
        this.loadBalanceRule = loadBalanceRule;
    }

    public Long getBeatIntervalTime() {
        return beatIntervalTime;
    }

    public void setBeatIntervalTime(Long beatIntervalTime) {
        this.beatIntervalTime = beatIntervalTime;
    }

    public Long getStopConnectTime() {
        return stopConnectTime;
    }

    public void setStopConnectTime(Long stopConnectTime) {
        this.stopConnectTime = stopConnectTime;
    }

    public String getSerializer() {
        return serializer;
    }

    public void setSerializer(String serializer) {
        this.serializer = serializer;
    }

    public String getCompressor() {
        return compressor;
    }

    public void setCompressor(String compressor) {
        this.compressor = compressor;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Integer getRetryNum() {
        return retryNum;
    }

    public void setRetryNum(Integer retryNum) {
        this.retryNum = retryNum;
    }

    public Integer getWeights() {
        return weights;
    }

    public void setWeights(Integer weights) {
        this.weights = weights;
    }

    public String getHealth() {
        return health;
    }

    public void setHealth(String health) {
        this.health = health;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }
}
