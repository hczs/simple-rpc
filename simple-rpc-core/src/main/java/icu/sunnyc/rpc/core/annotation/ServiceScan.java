package icu.sunnyc.rpc.core.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务扫描包
 * 指定服务实现类所在的包
 * @author: houcheng
 * @date: 2022/6/2 8:53:23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServiceScan {

    /**
     * 服务实现类所在包 数组
     */
    String[] basePackages() default {};
}
