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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

/**
 * RPC Server
 * @author: houcheng
 * @date: 2022/5/31 16:57:13
 */
@Slf4j
public class RpcServer implements ApplicationContextAware, InitializingBean {

    private String serviceAddress;

    private String registryAddress;

    /**
     * 存放接口名和服务对象（实现类对象）之间的映射关系
     */
    private Map<String, Object> handlerMap = new HashMap<>();

    public RpcServer(String serviceAddress, String registryAddress) {
        this.serviceAddress = serviceAddress;
        this.registryAddress = registryAddress;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

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
        channelFuture.addListener(future -> {
            if (future.isSuccess()) {
                log.info("RPC 服务器启动成功，监听端口：{}", port);
            }
        });

        // 服务注册
        ServerRegistry zookeeperServerRegistry = new ServerRegistry(registryAddress);
        zookeeperServerRegistry.register(registryAddress);
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
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
}
