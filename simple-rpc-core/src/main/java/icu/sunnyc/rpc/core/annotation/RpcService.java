package icu.sunnyc.rpc.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解用于标记在 RPC 服务的实现类上
 * @author: houcheng
 * @date: 2022/5/31 16:38:25
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 具体接口的 Class 对象
     * 因为可能会实现多个接口，所以需要指定
     */
    Class<?> value();

}
