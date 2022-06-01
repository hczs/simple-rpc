package icu.sunnyc.rpc.core.server;

import icu.sunnyc.rpc.core.constant.CommonConstant;
import icu.sunnyc.rpc.core.utils.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
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

    private final CuratorFramework curatorZkClient;

    public ServerRegistry(String zkAddress) {
        curatorZkClient = ZookeeperUtil.getCuratorZookeeperClient(zkAddress);
    }

    /**
     * 服务注册
     * @param serviceAddress 服务地址
     */
    public void register(String serviceAddress) {
        log.info("Registering service address {}", serviceAddress);
        byte[] data = serviceAddress.getBytes(StandardCharsets.UTF_8);
        String servicePath = CommonConstant.ZK_SERVICE_PATH_PREFIX;
        try {
            String resultPath = curatorZkClient.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
                    .forPath(servicePath, data);
            log.info("Service {} registered at path: {}", serviceAddress, resultPath);
        } catch (Exception e) {
            log.error("Registering service address {} failed", serviceAddress, e);
        }
    }
}
