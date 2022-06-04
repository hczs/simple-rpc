package icu.sunnyc.rpc.demo.consumer;

import icu.sunnyc.rpc.core.annotation.SimpleReference;
import icu.sunnyc.rpc.demo.api.HelloService;
import org.springframework.stereotype.Component;

/**
 * RPC 调用
 * @author hc
 * @date Created in 2022/6/4 11:53
 * @modified
 */
@Component
public class MyConsumer {

    @SimpleReference
    private HelloService helloService;

    public void sayHello(String hello) {
        System.out.println(helloService.hello(hello));
    }
}
