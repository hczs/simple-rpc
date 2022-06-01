package icu.sunnyc.rpc.core.utils;


import icu.sunnyc.rpc.core.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;

/**
 * @author : hc
 * @date : Created in 2022/5/31 21:02
 * @modified :
 */
@Slf4j
public class ZookeeperUtil {

    private ZookeeperUtil() {

    }

    /**
     * 创建 zookeeper连接
     * @param zkAddress zookeeper地址
     * @return CuratorFramework 对象
     */
    public static CuratorFramework getCuratorZookeeperClient(String zkAddress) {
        // 重试策略 重试 3 次，每次间隔 5 秒
        RetryNTimes retryPolicy = new RetryNTimes(CommonConstant.ZK_RETRY_TIMES, CommonConstant.ZK_RETRY_INTERVAL);
        // 创建连接
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zkAddress)
                .sessionTimeoutMs(CommonConstant.ZK_SESSION_TIMEOUT)
                .retryPolicy(retryPolicy)
                // 指定命名空间后，客户端所有操作都会以该命名空间为前缀
                .namespace(CommonConstant.ZK_NAMESPACE)
                .build();
        log.info("Connecting to zookeeper server...");
        // 启动
        client.start();
        log.info("Connected to zookeeper server");
        return client;
    }
}
