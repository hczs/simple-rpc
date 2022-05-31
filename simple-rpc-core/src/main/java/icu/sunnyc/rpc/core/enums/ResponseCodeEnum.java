package icu.sunnyc.rpc.core.enums;

/**
 * 返回响应码枚举类
 * @author ：hc
 * @date ：Created in 2022/5/31 20:23
 * @modified ：
 */
public enum ResponseCodeEnum {

    /**
     * 成功
     */
    SUCCESS(0),

    /**
     * 失败
     */
    FAIL(-1);

    private final int code;

    ResponseCodeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
