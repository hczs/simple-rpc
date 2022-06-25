package icu.sunnyc.rpc.demo.consumer;

import com.sun.xml.internal.ws.server.sei.SEIInvokerTube;
import icu.sunnyc.rpc.core.annotation.SimpleReference;
import icu.sunnyc.rpc.demo.api.HelloService;
import icu.sunnyc.rpc.demo.api.Student;
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

    public void callOneYearLater(Student student) {
        System.out.println("一年后的学生：" + helloService.oneYearLater(student));
    }
}
