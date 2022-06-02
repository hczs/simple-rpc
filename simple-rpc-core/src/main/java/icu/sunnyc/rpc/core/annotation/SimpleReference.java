package icu.sunnyc.rpc.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 作用在类属性上，实现自动注入 RPC 代理对象
 * @author: houcheng
 * @date: 2022/6/2 17:16:26
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface SimpleReference {
}
