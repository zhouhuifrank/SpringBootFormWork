package com.frankzhou.project.controller;

import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.service.PushService;
import com.frankzhou.project.websocket.domain.dto.WebSocketRequestDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.MediaType;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 服务端推送模块 SSE/WebSocket
 * @date 2023-09-24
 */
@RestController
@RequestMapping("/sse")
@Api(tags = {"服务端推送模块"})
public class PushController {

    @Resource
    private PushService pushService;

    @ApiOperation("建立连接")
    @GetMapping(path = "/message/{clientId}", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter getMessage(@PathVariable("clientId") String clientId) {
        SseEmitter emitter = pushService.getMessage(clientId);
        return emitter;
    }

    @ApiOperation("执行任务")
    @PostMapping("/executeTask")
    public ResponseBodyEmitter executeTask(@RequestBody WebSocketRequestDTO requestDTO, HttpServletResponse response) {
        ResponseBodyEmitter emitter = pushService.pushCompleteRate(requestDTO,response);
        return emitter;
    }

    @ApiOperation("关闭连接")
    @GetMapping(path = "/close/{clientId}")
    public ResultDTO<Boolean> closeSseConnection(@PathVariable("clientId") String clientId) {
        pushService.closeConnection(clientId);
        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }
}
