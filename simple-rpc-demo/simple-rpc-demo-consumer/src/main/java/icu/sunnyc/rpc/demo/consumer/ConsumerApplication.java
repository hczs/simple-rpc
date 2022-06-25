package icu.sunnyc.rpc.demo.consumer;


import icu.sunnyc.rpc.core.RpcApplication;
import icu.sunnyc.rpc.core.annotation.SimpleReference;
import icu.sunnyc.rpc.demo.api.HelloService;
import icu.sunnyc.rpc.demo.api.Student;
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

        // 复杂参数类型调用
        consumerApplication.callOneYearLater();
    }

    public String sayHello(String name) {
        return helloService.hello(name);
    }

    public void callOneYearLater() {
        Student student = new Student();
        student.setName("sunnyc");
        student.setAge(18);
        student.setGender("male");
        System.out.println("一年前的学生：" + student);
        System.out.println("一年后的学生：" + helloService.oneYearLater(student));
    }

}
