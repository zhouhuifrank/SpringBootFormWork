package com.frankzhou.project.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket-Netty服务器
 * @date 2023-09-24
 */
@Slf4j
@Configuration
public class NettyWebSocketServer {
    /**
     * WebSocket服务器端口
     */
    public static final int WEB_SOCKET_PORT = 8090;
    /**
     * 线程池执行器
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(NettyRuntime.availableProcessors());

    /**
     * 启动WebSocket服务
     */
    @PostConstruct
    public void start() throws InterruptedException {
        run();
        log.info("WebSocket服务启动成功，监听端口:{}",WEB_SOCKET_PORT);
    }

    @PreDestroy
    public void destroy() {
        Future<?> bossFuture = bossGroup.shutdownGracefully();
        Future<?> workerFuture = workerGroup.shutdownGracefully();
        bossFuture.syncUninterruptibly();
        workerFuture.syncUninterruptibly();
        log.info("关闭WebSocket服务器成功");
    }


    public void run() throws InterruptedException {
        // Netty服务器引导程序
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .option(ChannelOption.SO_KEEPALIVE,true)
                // 添加日志处理器
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        // 30秒内客户端不能没有向服务端发送心跳，则断开连接
                        pipeline.addLast(new IdleStateHandler(30,0,0));
                        // Websocket建立连接使用Http协议，因此需要http的编码器和解码器
                        pipeline.addLast(new HttpServerCodec());
                        // 以块的方式写数据
                        pipeline.addLast(new ChunkedWriteHandler());
                        // 数据聚合
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        /**
                         * 协议升级，将http协议升级为WebSocket协议，并且保持长连接
                         */
                        pipeline.addLast(new WebSocketServerProtocolHandler("/"));
                        // 自定义handler，处理业务逻辑
                        pipeline.addLast(new NettyWebSocketServerHandler());
                    }
                });
        // 启动服务器，监听端口，同步阻塞直到启动成功
        serverBootstrap.bind(WEB_SOCKET_PORT).sync();
    }
}
