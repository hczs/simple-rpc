package icu.sunnyc.rpc.core.client;

import icu.sunnyc.rpc.core.handler.RemoteInvocationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

/**
 * RPC Proxy
 * @author: houcheng
 * @date: 2022/6/1 15:42:32
 */
@Component
public class RpcProxy {

    @Autowired
    private ServiceDiscovery serviceDiscovery;

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
