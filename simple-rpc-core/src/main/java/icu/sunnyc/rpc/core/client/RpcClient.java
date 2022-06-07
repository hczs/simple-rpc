package icu.sunnyc.rpc.core.client;

import icu.sunnyc.rpc.core.codec.RpcDecoder;
import icu.sunnyc.rpc.core.codec.RpcEncoder;
import icu.sunnyc.rpc.core.entity.RpcRequest;
import icu.sunnyc.rpc.core.entity.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * RPC client / Netty Client
 * @author: houcheng
 * @date: 2022/6/1 16:07:30
 */
@Slf4j
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse>  {

    private String host;

    private int port;

    private RpcResponse response;

    private CountDownLatch countDownLatch;

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) {
        this.response = response;
        countDownLatch.countDown();
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("RpcClient exceptionCaught: ", cause);
        ctx.close();
    }

    public RpcResponse sendRequest(RpcRequest request) throws Exception {
        NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                                    .addLast(new RpcDecoder(RpcResponse.class))
                                    .addLast(RpcClient.this);
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // 这里改用 countDownLatch 而不用 obj wait()/notifyAll() 的方式了
            // 因为可能在 wait 之前就已经响应 notifyAll 了，造成假死等待
            countDownLatch = new CountDownLatch(1);
            channelFuture.channel().writeAndFlush(request).sync();
            log.info("RpcClient sendRequest: {}", request);
            log.info("Waiting for response...");
            countDownLatch.await();
            if (response != null) {
                channelFuture.channel().closeFuture().sync();
            }
            return response;
        } finally {
            eventLoopGroup.shutdownGracefully();
        }
    }
}
