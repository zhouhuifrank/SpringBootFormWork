package com.frankzhou.project.service;

import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.websocket.domain.dto.WebSocketRequestDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 服务端推送服务
 * @date 2023-09-24
 */
public interface PushService {

    /**
     * 建立连接，保存到map中
     */
    SseEmitter getSseEmitter(String clientId);

    /**
     * 发送消息，类似于ChatGPT的流式应答
     */
    void sendMessage(String clientId) throws IOException, InterruptedException;

    /**
     * 客户都安主动关闭连接
     */
    void closeConnection(String clientId);

    /**
     * 获取SSE推送数据
     */
    SseEmitter getMessage(String clientId);

    /**
     * 执行任务，获取任务进度
     */
    ResponseBodyEmitter pushCompleteRate(WebSocketRequestDTO requestDTO, HttpServletResponse response);
}
