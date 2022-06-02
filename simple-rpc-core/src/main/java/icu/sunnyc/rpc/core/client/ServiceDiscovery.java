package icu.sunnyc.rpc.core.client;

import icu.sunnyc.rpc.core.constant.ZookeeperConstant;
import icu.sunnyc.rpc.core.utils.ZookeeperUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Zookeeper Server Discovery
 * @author ：hc
 * @date ：Created in 2022/5/31 20:52
 * @modified ：
 */
@Slf4j
@Component
@PropertySource("classpath:simple-rpc.properties")
public class ServiceDiscovery {

    /**
     * 服务注册中心地址
     */
    @Value("${registry.address}")
    private String registryAddress;

    /**
     * 服务列表
     */
    private volatile List<String> serviceList = new ArrayList<>();

    @PostConstruct
    public void init() {
        // 注册永久监听
        watchNode();
    }

    /**
     * 返回一个可用的服务地址
     * 持续阻塞直到有可用的服务地址
     * @return 服务地址 ip:port 字符串
     */
    public String discover() {
        int size = serviceList.size();
        String result;
        // 如果服务列表为空，则返回null
        if (size == 0) {
            log.warn("No available service, please check the service registry address or service status ");
            return null;
        }
        // 如果服务列表只有一个服务地址，则直接返回
        if (size == 1) {
            result = serviceList.get(0);
            log.info("Only one service available, return it directly: {}", result);
            return result;
        }
        // 如果服务列表大于 1 个，则随机返回一个服务地址
        result = serviceList.get(ThreadLocalRandom.current().nextInt(size));
        log.info("Randomly select a service: {}", result);
        return result;
    }

    /**
     * 注册永久监听
     */
    private void watchNode() {
        log.info("Start watching the service registry node: {}", ZookeeperConstant.ZK_REGISTRY_PATH);
        log.info("Service registry address: {}", registryAddress);
        CuratorFramework curatorZkClient = ZookeeperUtil.getCuratorZookeeperClient(registryAddress);
        PathChildrenCache pathChildrenCache = new PathChildrenCache(curatorZkClient, ZookeeperConstant.ZK_REGISTRY_PATH, true);
        try {
            // 同步初始化 初始化后即可获取到当前服务列表
            pathChildrenCache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
            // 初次加载 获取服务列表
            flushServiceList(pathChildrenCache);
            // 添加永久监听 监听节点变化 并及时刷新服务列表
            pathChildrenCache.getListenable().addListener((client, event) -> {
                log.info("Node change event: {}", event.getType());
                // 监听到子节点变化 刷新服务列表
                flushServiceList(pathChildrenCache);
            });
        } catch (Exception e) {
            log.error("An exception occurred while listening for the change of zookeeper node", e);
        }

    }

    /**
     * 刷新服务列表
     * @param pathChildrenCache PathChildrenCache
     */
    private void flushServiceList(PathChildrenCache pathChildrenCache) {
        List<ChildData> childDataList = pathChildrenCache.getCurrentData();
        ArrayList<String> curServiceList = new ArrayList<>();
        childDataList.forEach(childData -> curServiceList.add(new String(childData.getData())));
        serviceList = curServiceList;
    }
}
