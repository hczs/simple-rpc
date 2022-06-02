package icu.sunnyc.rpc.demo.producer.impl;

import icu.sunnyc.rpc.core.annotation.RpcService;
import icu.sunnyc.rpc.demo.api.HelloService;

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
}
