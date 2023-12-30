package com.frankzhou.project.service;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.frankzhou.project.model.dto.chatgpt.OpenAiChatDTO;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 流式应答demo
 * @date 2023-12-11
 */
public interface AiChatService {

    ResponseBodyEmitter doChatByStream(OpenAiChatDTO chatDTO, HttpServletRequest request, HttpServletResponse response) throws IOException, NoApiKeyException, InputRequiredException;
}
