package icu.sunnyc.rpc.core.client;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Zookeeper Server Discovery
 * @author ：hc
 * @date ：Created in 2022/5/31 20:52
 * @modified ：
 */
@Slf4j
public class ServerDiscovery {

    /**
     * 服务列表
     */
    private volatile List<String> dataList = new ArrayList<>();

    /**
     * 服务注册中心地址
     */
    private String registryAddress;

    public ServerDiscovery(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    private void watchNode() {

    }
}
