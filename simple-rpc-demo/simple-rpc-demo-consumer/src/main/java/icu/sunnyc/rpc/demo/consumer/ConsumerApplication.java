package icu.sunnyc.rpc.demo.consumer;


import icu.sunnyc.rpc.core.RpcApplication;
import icu.sunnyc.rpc.core.annotation.SimpleReference;
import icu.sunnyc.rpc.demo.api.HelloService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * RPC 调用测试
 * @author: houcheng
 * @date: 2022/6/1 16:43:05
 */
@Component
public class ConsumerApplication {

    @SimpleReference
    private HelloService helloService;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = RpcApplication.run(ConsumerApplication.class);
        ConsumerApplication consumerApplication = context.getBean(ConsumerApplication.class);
        Random random = new Random();
        // 并发调用
        ConcurrencyUtil.concurrencyExecute(100, item -> {
            int reqParam = random.nextInt(100);
            String result = item.sayHello(String.valueOf(reqParam));
            System.out.println("请求参数：" + reqParam + " 响应结果：" + result);
        }, consumerApplication);
    }

    public String sayHello(String name) {
        return helloService.hello(name);
    }

}
