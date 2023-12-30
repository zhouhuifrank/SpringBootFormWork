package com.frankzhou.project.Util;

import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MessageManager;
import com.alibaba.dashscope.common.ResultCallback;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.frankzhou.project.service.AiChatService;
import io.reactivex.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.concurrent.Semaphore;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 大模型
 * @date 2023-12-07
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class DashScopeTests {

//    @Value("${dash.scope.api.key}")
//    private String apiKey;

    @Resource
    private AiChatService aiChatService;

    private String apiKey = "sk-ab0ada29ccd840f6b342c4af0f670a7f";

    @Test
    public void testDoOnceChatWithMessage() throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message promptMsg = Message.
                builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一個説唱歌手")
                .build();
        Message userMsg = Message
                .builder()
                .role(Role.USER.getValue())
                .content("從你一名説唱歌手的角度，評價五月天假唱")
                .build();
        MessageManager msgManager = new MessageManager();
        msgManager.add(promptMsg);
        msgManager.add(userMsg);

        QwenParam param =
                QwenParam.builder()
                        .model(Generation.Models.QWEN_MAX)
                        .messages(msgManager.get())
                        .resultFormat(QwenParam.ResultFormat.MESSAGE)
                        .topP(0.8)
                        .apiKey(apiKey)
                        .enableSearch(true)
                        .build();

        GenerationResult result = gen.call(param);
        log.info("chat result:{}", JSONUtil.toJsonStr(result));
    }

    @Test
    public void testDoChatOnceWithPrompt() throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        QwenParam questionParam = QwenParam.builder().model(Generation.Models.QWEN_MAX)
                .prompt("你是一個説唱歌手")
                .prompt("請你以我是小狗編一首歌")
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .enableSearch(true)
                .build();

        GenerationResult result = gen.call(questionParam);
        log.info("chat result:{}", JSONUtil.toJsonStr(result));
    }

    @Test
    public void testDoChatOnceWithCallback() throws NoApiKeyException, InputRequiredException, InterruptedException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一條小狗")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("此刻，你最想說什麽?")
                .build();

        MessageManager msgManager = new MessageManager();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam questionParam = QwenParam.builder()
                .model(Generation.Models.QWEN_MAX)
                .messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .enableSearch(true)
                .build();

        Semaphore semaphore = new Semaphore(0);
        gen.call(questionParam, new ResultCallback<GenerationResult>() {
            @Override
            public void onEvent(GenerationResult generationResult) {
                log.info("chat result:{}", JSONUtil.toJsonStr(generationResult));
            }

            @Override
            public void onComplete() {
                log.error("chat complete");
                semaphore.release();
            }

            @Override
            public void onError(Exception e) {
                log.error("chat error");
                semaphore.release();
            }
        });

        semaphore.acquire();
    }

    @Test
    public void testDoChatStream() throws NoApiKeyException, InputRequiredException {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("你是一條小狗")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content("此刻，你最想說什麽?")
                .build();

        MessageManager msgManager = new MessageManager();
        msgManager.add(systemMsg);
        msgManager.add(userMsg);
        QwenParam questionParam = QwenParam.builder()
                .model(Generation.Models.QWEN_MAX)
                .messages(msgManager.get())
                .resultFormat(QwenParam.ResultFormat.MESSAGE)
                .topP(0.8)
                .apiKey(apiKey)
                .enableSearch(true)
                .build();

        Flowable<GenerationResult> resultStream = gen.streamCall(questionParam);
        StringBuffer fullMessage = new StringBuffer();
        resultStream.blockingForEach(message -> {
            fullMessage.append(message.getOutput().getChoices().get(0).getMessage().getContent());
            log.info("stream message:{}", JSONUtil.toJsonStr(message));
        });

        String result = fullMessage.toString();
        log.info("chat result:{}", result);
    }
}
