package icu.sunnyc.rpc.core.handler;

import icu.sunnyc.rpc.core.client.RpcClient;
import icu.sunnyc.rpc.core.client.ServiceDiscovery;
import icu.sunnyc.rpc.core.entity.RpcRequest;
import icu.sunnyc.rpc.core.entity.RpcResponse;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * RPC 客户端远程调用具体处理器
 * @author: houcheng
 * @date: 2022/6/1 15:56:36
 */
@Slf4j
public class RemoteInvocationHandler implements InvocationHandler {

    private ServiceDiscovery serviceDiscovery;

    public RemoteInvocationHandler(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setInterfaceName(method.getDeclaringClass().getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        // 服务发现 找一个具体能够处理请求的服务地址
        String serviceAddress = serviceDiscovery.discover();
        if (serviceAddress == null) {
            return null;
        }
        String[] hostAndPort = serviceAddress.split(":");
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);
        // 发送请求
        RpcClient rpcClient = new RpcClient(host, port);
        RpcResponse rpcResponse = rpcClient.sendRequest(rpcRequest);
        log.info("rpcResponse: {}", rpcResponse);
        return rpcResponse.getResult();
    }
}
