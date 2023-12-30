package com.frankzhou.project.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.frankzhou.project.model.dto.chatgpt.OpenAiChatDTO;
import com.frankzhou.project.service.AiChatService;
import com.frankzhou.project.service.UserService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-12-11
 */
@Slf4j
@Service
public class AiChatServiceImpl implements AiChatService {

    @Resource
    private UserService userService;

    @Value("${dash.scope.api.key}")
    private String apiKey;

    @Override
    public ResponseBodyEmitter doChatByStream(OpenAiChatDTO chatDTO, HttpServletRequest request, HttpServletResponse response) throws IOException, NoApiKeyException, InputRequiredException {
        ResponseBodyEmitter emitter = buildSseEmitter(response);

        emitter.onCompletion(() -> {
            log.info("回答结束，使用模型为{}:{}",chatDTO.getModelType(),chatDTO.getModelCode());
        });

        emitter.onError(err -> {
            log.info("回答请求异常，使用模型为{}:{}",chatDTO.getModelType(),chatDTO.getModelCode());
        });

        emitter.onTimeout(() -> {
            log.info("回答请求超时，使用模型为{}:{}",chatDTO.getModelType(),chatDTO.getModelCode());
        });

        if (ObjectUtil.isNull(chatDTO) || CollectionUtil.isEmpty(chatDTO.getMessageList())) {
            emitter.send("请求参数错误");
            emitter.complete();
            return emitter;
        }

        /**
         * 构建参数
         */
        List<Message> promptList = chatDTO.getMessageList().stream().map(message -> {
            Message promptMsg = Message.builder()
                    .role(Role.USER.getValue())
                    .content(message.getContent())
                    .build();

            return promptMsg;
        }).collect(Collectors.toList());

        MessageManager msgManager = new MessageManager();
        promptList.forEach(prompt -> {
            msgManager.add(prompt);
        });

        QwenParam questionParam =
                QwenParam.builder()
                        .model(Generation.Models.QWEN_MAX)
                        .messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .apiKey(apiKey)
                        .enableSearch(true)
                        .build();

        // 输入参数，等待回答，使用流式接口
        Generation gen = new Generation();
        // IOException, NoApiKeyException, InputRequiredException
        Flowable<GenerationResult> streamResult = gen.streamCall(questionParam);
        StringBuffer fullResult = new StringBuffer();
        streamResult.blockingForEach(delta -> {
            String requestId = delta.getRequestId();
            Integer inputTokens = delta.getUsage().getInputTokens();
            log.info("请求编号request id:{} 用户使用token数量:{}",requestId,inputTokens);
            String responseMsg = delta.getOutput().getChoices().get(0).getMessage().getContent();
            log.info("回答:{}",responseMsg);
            emitter.send(responseMsg);
            fullResult.append(responseMsg);
        });

        emitter.complete();
        String fullContent = fullResult.toString();
        log.info("Ai完整回答:{}",fullContent);
        return emitter;
    }

    private ResponseBodyEmitter buildSseEmitter(HttpServletResponse response) {
        // 设置响应头，流式输出，编码，禁用缓存
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");

        final ResponseBodyEmitter emitter = new ResponseBodyEmitter(5*60*1000L);
        return emitter;
    }
}
