package icu.sunnyc.rpc.core.server;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * RPC Server
 * @author: houcheng
 * @date: 2022/5/31 16:57:13
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serviceAddress;

    private String registryAddress;

    public RpcServer(String serviceAddress, String registryAddress) {
        this.serviceAddress = serviceAddress;
        this.registryAddress = registryAddress;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

    }
}
