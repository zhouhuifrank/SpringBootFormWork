package com.frankzhou.project.websocket;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.frankzhou.project.websocket.domain.dto.WebSocketRequestDTO;
import com.frankzhou.project.websocket.domain.enums.WebSocketRequestTypeEnum;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket
 * @date 2023-09-24
 */
@Slf4j
public class NettyWebSocketServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            log.info("握手完成");
        } else if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleEvent = (IdleStateEvent) evt;
            if (idleEvent.state() == IdleState.READER_IDLE) {
                log.info("读空闲，需要断开连接");
                // TODO 用户下线
                ctx.channel().close();
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, TextWebSocketFrame message) throws Exception {
        String text = message.text();
        log.info("接受到WebSocket请求，消息体:{}",text);
        WebSocketRequestDTO requestDTO = JSONUtil.toBean(text, WebSocketRequestDTO.class);
        Integer type = requestDTO.getType();
        WebSocketRequestTypeEnum requestType = WebSocketRequestTypeEnum.getEnumByCode(type);
        switch (requestType) {
            case HEART:
                log.info("心跳检测");
                break;
            case LOGIN:
                log.info("用户登录");
                context.writeAndFlush(new TextWebSocketFrame("登录成功"));
                break;
            case EXECUTE_TASK:
                log.info("执行任务");
                // 执行任务，推送任务进度
                break;
            default:
                log.info("未知类型");
        }
    }
}
