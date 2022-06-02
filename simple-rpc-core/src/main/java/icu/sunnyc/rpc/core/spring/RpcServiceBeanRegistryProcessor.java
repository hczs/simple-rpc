package icu.sunnyc.rpc.core.spring;

import icu.sunnyc.rpc.core.annotation.RpcService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.Set;

/**
 * Rpc 服务 Bean 动态注册到 Spring 容器
 * 在 Spring 容器启动的时候会执行 BeanDefinitionRegistryPostProcessor 的 postProcessBeanDefinitionRegistry 方法
 * 大概意思就是等 beanDefinition 加载完毕之后，对 beanDefinition 进行后置处理，可以在此进行调整 IOC 容器中的 beanDefinition
 * @author: houcheng
 * @date: 2022/6/2 11:28:45
 */
@Slf4j
public class RpcServiceBeanRegistryProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * 扫描包路径 将这些包下的 bean 加载到 Spring 容器中
     * 扫描 {@link RpcService} 和 {@link Component} 注解的类
     */
    private final String[] basePackages;

    public RpcServiceBeanRegistryProcessor(String[] basePackages) {
        this.basePackages = basePackages;
    }


    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        // ClassPathScanningCandidateComponentProvider 可以根据一定的规则扫描类路径下满足特定条件的 Class
        // 构造参数 useDefaultFilter 指定是否需要使用默认的TypeFilter 默认的 TypeFilter 只扫描 @Component、@Service、@Repository、@Controller
        // lassPathScanningCandidateComponentProvider 在扫描时可以通过 TypeFilter 来过滤类
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter((metadataReader, metadataReaderFactory) -> {
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            return annotationMetadata.hasAnnotation(RpcService.class.getName()) ||
                    metadataReader.getAnnotationMetadata().hasAnnotation(Component.class.getName());
        });
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents(basePackage);
            for (BeanDefinition beanDefinition : candidateComponents) {
                // 将 beanDefinition 注册到 Spring 容器中
                beanDefinitionRegistry.registerBeanDefinition(Objects.requireNonNull(beanDefinition.getBeanClassName()), beanDefinition);
                log.debug("RpcServiceBeanRegistryProcessor 注册 beanDefinition 到 Spring 容器中，beanName={}", beanDefinition.getBeanClassName());
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
