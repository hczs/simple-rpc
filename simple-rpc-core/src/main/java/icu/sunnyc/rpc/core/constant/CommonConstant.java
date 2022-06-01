package icu.sunnyc.rpc.core.constant;

/**
 * 存储常量
 * @author: houcheng
 * @date: 2022/5/31 17:01:36
 */
public final class CommonConstant {

    /**
     * Zookeeper 连接超时时间
     */
    public static final int ZK_SESSION_TIMEOUT = 5000;

    /**
     * Zookeeper 连接超时重试次数
     */
    public static final int ZK_RETRY_TIMES = 5;

    /**
     * Zookeeper 连接超时重试间隔 ms
     */
    public static final int ZK_RETRY_INTERVAL = 1000;

    /**
     * Zookeeper 命名空间 客户端所有操作都会以该命名空间为前缀
     * 命名空间节点默认是容器节点 若 ZK 不支持容器节点 类型会是持久节点
     */
    public static final String ZK_NAMESPACE = "simple-rpc";

    /**
     * Zookeeper 服务注册节点 所有服务都会在该节点下注册 client 从该节点下获取服务列表
     */
    public static final String ZK_REGISTRY_PATH = "/registry";

    /**
     * 服务数据存储路径前缀
     */
    public static final String ZK_SERVICE_PATH_PREFIX = ZK_REGISTRY_PATH + "/service";

}
