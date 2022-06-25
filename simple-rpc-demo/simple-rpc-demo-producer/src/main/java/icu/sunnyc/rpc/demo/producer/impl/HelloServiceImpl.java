package icu.sunnyc.rpc.demo.producer.impl;

import icu.sunnyc.rpc.core.annotation.RpcService;
import icu.sunnyc.rpc.demo.api.HelloService;
import icu.sunnyc.rpc.demo.api.Student;

/**
 * HelloService 实现类
 * @author: houcheng
 * @date: 2022/5/31 16:30:43
 */
@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Hello " + name;
    }

    @Override
    public Student oneYearLater(Student student) {
        System.out.println("生产者收到学生信息：" + student);
        // 一年后年龄 + 1
        student.setAge(student.getAge() + 1);
        return student;
    }
}
