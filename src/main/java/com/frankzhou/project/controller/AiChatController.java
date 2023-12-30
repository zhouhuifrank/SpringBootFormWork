package com.frankzhou.project.controller;

import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.frankzhou.project.model.dto.chatgpt.OpenAiChatDTO;
import com.frankzhou.project.service.AiChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-12-11
 */
@Api(tags = {"Ai应答"})
@RestController
@RequestMapping("/ai")
public class AiChatController {

    @Resource
    private AiChatService aiChatService;

    @ApiOperation(value = "进行问答")
    @PostMapping("/doChat")
    public ResponseBodyEmitter doStreamChat(@RequestBody OpenAiChatDTO openAiChatDTO, HttpServletRequest request, HttpServletResponse response) throws NoApiKeyException, InputRequiredException, IOException {
        return aiChatService.doChatByStream(openAiChatDTO,request,response);
    }
}
