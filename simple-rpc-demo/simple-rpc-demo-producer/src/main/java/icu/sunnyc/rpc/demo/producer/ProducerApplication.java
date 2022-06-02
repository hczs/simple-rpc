package icu.sunnyc.rpc.demo.producer;

import icu.sunnyc.rpc.core.RpcApplication;

/**
 * RPC 服务启动类 启动自动注册服务
 * @author: houcheng
 * @date: 2022/5/31 16:50:03
 */
public class ProducerApplication {

    public static void main(String[] args) {
        RpcApplication.run(ProducerApplication.class);
    }
}
