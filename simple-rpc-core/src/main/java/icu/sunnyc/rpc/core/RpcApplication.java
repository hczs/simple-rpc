package icu.sunnyc.rpc.core;


import icu.sunnyc.rpc.core.annotation.ServiceScan;
import icu.sunnyc.rpc.core.constant.CommonConstant;
import icu.sunnyc.rpc.core.server.RpcServer;
import icu.sunnyc.rpc.core.spring.RpcServiceBeanRegistryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;

/**
 * RPC 启动类
 * @author: houcheng
 * @date: 2022/6/2 8:48:40
 */
@Slf4j
public class RpcApplication {

    private RpcApplication() {}

    public static AnnotationConfigApplicationContext run(Class<?> cls) {
        log.info("RpcApplication start");
        // 默认扫描 rpc core 包下的所有类
        String[] basePackages = new String[]{RpcApplication.class.getPackage().getName()};
        basePackages = addServicePackages(cls, basePackages);
        // 手动扫描包并注册 bean
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        RpcServiceBeanRegistryProcessor registryProcessor = new RpcServiceBeanRegistryProcessor(basePackages);
        applicationContext.addBeanFactoryPostProcessor(registryProcessor);
        applicationContext.refresh();
        log.info("下面打印 Sring 的 Bean 列表");
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(log::info);
        // 启动 RPC 服务
        RpcServer rpcServer = applicationContext.getBean(RpcServer.class);
        rpcServer.start(applicationContext);
        return applicationContext;
    }

    /**
     * 添加应用扫描包
     * 如果应用启动类上有 @ServiceScan 注解，则优先使用注解的值
     * 如果应用启动类上没有 @ServiceScan 注解，则使用启动类所在包
     * @param cls 应用启动类
     * @param basePackages 默认扫描包
     * @return 所有扫描包
     */
    private static String[] addServicePackages(Class<?> cls, String[] basePackages) {
        ServiceScan serviceScanAnnotation = cls.getAnnotation(ServiceScan.class);
        if (serviceScanAnnotation == null) {
            return ArrayUtils.add(basePackages, cls.getPackage().getName());
        }
        String[] servicePackages = cls.getAnnotation(ServiceScan.class).basePackages();
        log.info("Get service scan package path：{}", Arrays.toString(servicePackages));
        return ArrayUtils.addAll(basePackages, servicePackages);
    }

}
