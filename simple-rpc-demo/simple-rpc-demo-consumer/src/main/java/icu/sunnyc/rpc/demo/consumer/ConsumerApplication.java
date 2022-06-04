package icu.sunnyc.rpc.demo.consumer;


import icu.sunnyc.rpc.core.RpcApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * RPC 调用测试
 * @author: houcheng
 * @date: 2022/6/1 16:43:05
 */
@Component
public class ConsumerApplication {

    @Autowired
    private MyConsumer myConsumer;

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = RpcApplication.run(ConsumerApplication.class);
        ConsumerApplication consumerApplication = context.getBean(ConsumerApplication.class);
        consumerApplication.sayHello();
    }

    public void sayHello() {
        myConsumer.sayHello("sunnyc");
    }


}
