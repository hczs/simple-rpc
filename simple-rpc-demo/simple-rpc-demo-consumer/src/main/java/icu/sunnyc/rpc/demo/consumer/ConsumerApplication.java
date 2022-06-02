package icu.sunnyc.rpc.demo.consumer;


import icu.sunnyc.rpc.core.RpcApplication;
import icu.sunnyc.rpc.core.client.RpcProxy;
import icu.sunnyc.rpc.demo.api.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * RPC 调用测试
 * @author: houcheng
 * @date: 2022/6/1 16:43:05
 */
public class ConsumerApplication {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = RpcApplication.run(ConsumerApplication.class);
        RpcProxy rpcProxy = context.getBean(RpcProxy.class);
        HelloService helloService = rpcProxy.create(HelloService.class);
        System.out.println(helloService.hello("sunnyc"));
    }
}
