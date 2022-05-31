package icu.sunnyc.rpc.demo.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * RPC 服务启动类 启动自动注册服务
 * @author: houcheng
 * @date: 2022/5/31 16:50:03
 */
public class RpcBootstrap {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("spring.xml");
    }
}
