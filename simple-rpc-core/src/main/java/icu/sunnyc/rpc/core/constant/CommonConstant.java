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
     * Zookeeper 所有服务注册地址
     */
    public static final String ZK_REGISTRY_PATH = "simple-rpc/registry";

    /**
     * 服务数据存储路径
     */
    public static final String ZK_DATA_PATH = "/data";

}
