package icu.sunnyc.rpc.core.server;

import icu.sunnyc.rpc.core.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;

import java.nio.charset.StandardCharsets;

/**
 * Zookeeper Server Registry
 * @author: houcheng
 * @date: 2022/5/31 16:58:41
 */
@Slf4j
public class ZookeeperServerRegistry {

    private CuratorFramework zkClient = null;

    public ZookeeperServerRegistry(String zkAddress) {
        // 重试策略 重试 3 次，每次间隔 5 秒
        RetryNTimes retryPolicy = new RetryNTimes(CommonConstant.ZK_RETRY_TIMES, CommonConstant.ZK_RETRY_INTERVAL);
        // 创建连接
        zkClient = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(CommonConstant.ZK_SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                // 指定命名空间后，客户端所有操作都会以该命名空间为前缀
                .namespace(CommonConstant.ZK_REGISTRY_PATH).build();
        zkClient.start();
        log.info("Connecting to Zookeeper server {}", zkAddress);
    }

    public void register(String serviceName, String serviceAddress) {
        log.info("Registering service {} with address {}", serviceName, serviceAddress);
        byte[] data = serviceAddress.getBytes(StandardCharsets.UTF_8);
        try {
            String resultPath = zkClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(serviceName, data);
            log.info("Service {} registered with address {}", serviceName, resultPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Registering service {} with address {} failed", serviceName, serviceAddress, e);
        }
    }
}
