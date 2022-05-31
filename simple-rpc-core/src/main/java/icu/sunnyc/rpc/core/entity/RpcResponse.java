package icu.sunnyc.rpc.core.entity;

import lombok.Data;

/**
 * RPC 响应对象
 * @author: houcheng
 * @date: 2022/5/31 16:09:27
 */
@Data
public class RpcResponse {

    /**
     * 请求ID
     */
    private String requestId;

    /**
     * 响应结果
     */
    private Object result;

    /**
     * 状态码
     */
    private Integer code;

    /**
     * 错误信息
     */
    private Throwable errorMsg;
}
