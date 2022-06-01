package icu.sunnyc.rpc.demo.client;


import icu.sunnyc.rpc.core.client.RpcProxy;
import icu.sunnyc.rpc.demo.api.HelloService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RPC 调用测试
 * @author: houcheng
 * @date: 2022/6/1 16:43:05
 */
public class HelloServiceTest {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
        RpcProxy rpcProxy = (RpcProxy) context.getBean("rpcProxy");
        HelloService helloService = rpcProxy.create(HelloService.class);
        System.out.println(helloService.hello("sunnyc"));
    }
}
