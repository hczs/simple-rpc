package icu.sunnyc.rpc.core.server;

import icu.sunnyc.rpc.core.constant.CommonConstant;
import icu.sunnyc.rpc.core.utils.ZookeeperUtil;
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
public class ServerRegistry {

    private CuratorFramework zkClient = null;

    public ServerRegistry(String zkAddress) {
        zkClient = ZookeeperUtil.getZookeeperClient(zkAddress);
        zkClient.start();
        log.info("Connecting to Zookeeper server {}", zkAddress);
    }

    public void register(String serviceAddress) {
        log.info("Registering service address {}", serviceAddress);
        byte[] data = serviceAddress.getBytes(StandardCharsets.UTF_8);
        try {
            String resultPath = zkClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(CommonConstant.ZK_DATA_PATH, data);
            log.info("Service registered address {}", resultPath);
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Registering service address {} failed", serviceAddress, e);
        }
    }
}
