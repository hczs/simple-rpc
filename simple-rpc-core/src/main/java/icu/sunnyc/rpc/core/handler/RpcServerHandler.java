package icu.sunnyc.rpc.core.handler;

import icu.sunnyc.rpc.core.entity.RpcRequest;
import icu.sunnyc.rpc.core.entity.RpcResponse;
import icu.sunnyc.rpc.core.enums.ResponseCodeEnum;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.util.Map;

/**
 * RPC 请求处理器
 * @author ：hc
 * @date ：Created in 2022/5/31 20:10
 * @modified ：
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final Map<String, Object> handlerMap;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) {
        log.info("server receive request: {}", request);
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setRequestId(request.getRequestId());
        try {
            // 根据请求调用具体方法
            Object result = handle(request);
            rpcResponse.setResult(result);
            rpcResponse.setCode(ResponseCodeEnum.SUCCESS.getCode());
        } catch (Throwable t) {
            rpcResponse.setError(t);
            rpcResponse.setCode(ResponseCodeEnum.FAIL.getCode());
        }
        ctx.writeAndFlush(rpcResponse).addListener(ChannelFutureListener.CLOSE);
        log.info("server send response: {}", rpcResponse);
    }

    private Object handle(RpcRequest request) throws Throwable {
        String interfaceName = request.getInterfaceName();
        // 根据传入的接口名称 找到服务端实现接口的 Bean
        Object serviceBean = handlerMap.get(interfaceName);
        // 通过反射执行方法
        FastClass fastClass = FastClass.create(serviceBean.getClass());
        FastMethod method = fastClass.getMethod(request.getMethodName(), request.getParameterTypes());
        return method.invoke(serviceBean, request.getParameters());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception", cause);
        ctx.close();
    }
}
