package icu.sunnyc.rpc.core;


import icu.sunnyc.rpc.core.annotation.ServiceScan;
import icu.sunnyc.rpc.core.annotation.SimpleReference;
import icu.sunnyc.rpc.core.client.RpcProxy;
import icu.sunnyc.rpc.core.constant.CommonConstant;
import icu.sunnyc.rpc.core.server.RpcServer;
import icu.sunnyc.rpc.core.spring.RpcServiceBeanRegistryProcessor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;

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
        // 启动 Spring
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        // 注册 Bean
        registerRpcAndServiceBean(applicationContext, basePackages);
        // 注入客户端代理对象
        try {
            injectClientProxy(applicationContext);
        } catch (Exception e) {
            log.error("Inject client proxy error", e);
        }
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

    /**
     * 注册 RPC 和 应用中 Rpc Service Bean
     * @param applicationContext AnnotationConfigApplicationContext
     * @param basePackages 扫描包
     */
    private static void registerRpcAndServiceBean(AnnotationConfigApplicationContext applicationContext, String[] basePackages) {
        RpcServiceBeanRegistryProcessor registryProcessor = new RpcServiceBeanRegistryProcessor(basePackages);
        applicationContext.addBeanFactoryPostProcessor(registryProcessor);
        applicationContext.refresh();
        log.info("下面打印 Spring 的 Bean 列表");
        Arrays.stream(applicationContext.getBeanDefinitionNames()).forEach(log::info);
    }

    /**
     * 注入客户端代理对象
     * @param applicationContext AnnotationConfigApplicationContext
     * @throws IllegalAccessException IllegalAccessException
     */
    private static void injectClientProxy(AnnotationConfigApplicationContext applicationContext) throws IllegalAccessException {
        RpcProxy rpcProxy = applicationContext.getBean(RpcProxy.class);
        String[] beans = applicationContext.getBeanDefinitionNames();
        for (String bean : beans) {
            Class<?> beanClass = applicationContext.getType(bean);
            assert beanClass != null;
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(SimpleReference.class)) {
                    declaredField.setAccessible(true);
                    declaredField.set(applicationContext.getBean(bean), rpcProxy.create(declaredField.getType()));
                }
            }
        }
    }

}
