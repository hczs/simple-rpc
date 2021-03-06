package icu.sunnyc.rpc.core.server;

import icu.sunnyc.rpc.core.annotation.RpcService;
import icu.sunnyc.rpc.core.codec.RpcDecoder;
import icu.sunnyc.rpc.core.codec.RpcEncoder;
import icu.sunnyc.rpc.core.entity.RpcRequest;
import icu.sunnyc.rpc.core.entity.RpcResponse;
import icu.sunnyc.rpc.core.handler.RpcServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC Server / Netty Server
 * @author: houcheng
 * @date: 2022/5/31 16:57:13
 */
@Slf4j
@Component
@PropertySource("classpath:simple-rpc.properties")
public class RpcServer {

    /**
     * 注册中心地址
     */
    @Value("${registry.address:}")
    private String registryAddress;

    /**
     * RPC 服务启动地址
     */
    @Value("${service.address:}")
    private String serviceAddress;

    /**
     * 存放接口名和服务对象（实现类对象）之间的映射关系
     */
    private final Map<String, Object> handlerMap = new HashMap<>();

    /**
     * RpcServer 启动
     * @param applicationContext ApplicationContext
     */
    public void start(ApplicationContext applicationContext) {
        log.info("注册中心地址：{}", registryAddress);
        log.info("RPC 服务启动地址：{}", serviceAddress);
        if (StringUtils.isBlank(registryAddress) || StringUtils.isBlank(serviceAddress)) {
            log.warn("RPC 服务启动失败，请检查是否配置了 registry.address 和 service.address");
            return;
        }
        scanRpcServiceBean(applicationContext);
        startRpcServer();
    }

    /**
     * 获取 Spring 中所有带 RpcService 注解的 Bean
     * @param applicationContext ApplicationContext
     */
    private void scanRpcServiceBean(ApplicationContext applicationContext) {
        // 扫描所有带 RpcService 注解的 Bean
        Map<String, Object> serviceBeanMap = applicationContext.getBeansWithAnnotation(RpcService.class);
        if (serviceBeanMap.isEmpty()) {
            log.warn("No service bean found");
        }
        for (Object serviceBean : serviceBeanMap.values()) {
            // 获取 RpcService 注解的 value 值
            String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
            handlerMap.put(interfaceName, serviceBean);
        }
    }

    private void startRpcServer() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            // 服务端 对请求解码 对响应编码
                            ch.pipeline().addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    // 服务端处理 RPC 请求的 Handler
                                    .addLast(new RpcServerHandler(handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            // 解析服务地址
            String[] array = serviceAddress.split(":");
            String host = array[0];
            int port = Integer.parseInt(array[1]);

            // 启动 RPC 服务
            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            log.info("RPC 服务器启动成功，监听端口：{}", port);
            // 服务注册
            ServiceRegistry zookeeperServiceRegistry = new ServiceRegistry(registryAddress);
            zookeeperServiceRegistry.register(serviceAddress);
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("RpcServer start error", e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
