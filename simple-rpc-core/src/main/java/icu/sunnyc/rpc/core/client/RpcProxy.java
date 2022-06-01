package icu.sunnyc.rpc.core.client;

import icu.sunnyc.rpc.core.handler.RemoteInvocationHandler;

import java.lang.reflect.Proxy;

/**
 * RPC Proxy
 * @author: houcheng
 * @date: 2022/6/1 15:42:32
 */
public class RpcProxy {

    private final ServiceDiscovery serviceDiscovery;

    public RpcProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建代理
     * @param interfaceClass 接口全限定名
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T create(Class<?> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass}, new RemoteInvocationHandler(serviceDiscovery));
    }

}
