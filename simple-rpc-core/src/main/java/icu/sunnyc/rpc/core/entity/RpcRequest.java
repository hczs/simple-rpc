package icu.sunnyc.rpc.core.entity;

import lombok.Data;

/**
 * RPC 请求对象
 * @author: houcheng
 * @date: 2022/5/31 16:07:06
 */
@Data
public class RpcRequest {

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 接口名
     */
    private String interfaceName;

    /**
     * 方法名
     */
    private String methodName;

    private String serviceVersion;

    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数
     */
    private Object[] parameters;
}
