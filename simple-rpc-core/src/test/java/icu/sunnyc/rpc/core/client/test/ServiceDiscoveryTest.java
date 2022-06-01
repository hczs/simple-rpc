package icu.sunnyc.rpc.core.client.test;

import icu.sunnyc.rpc.core.client.ServiceDiscovery;
import org.junit.jupiter.api.Test;

/**
 * 服务发现测试
 * @author: houcheng
 * @date: 2022/6/1 15:04:40
 */
class ServiceDiscoveryTest {

    @Test
    void DiscoveryTest() throws InterruptedException {
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("127.0.0.1:2181");
        while (true) {
            System.out.println(serviceDiscovery.discover());
            Thread.sleep(1000);
        }
    }
}
