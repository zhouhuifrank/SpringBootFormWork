package com.frankzhou.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.model.dto.catalog.CatalogDTO;
import com.frankzhou.project.model.entity.Catalog;
import com.frankzhou.project.service.CatalogService;
import com.frankzhou.project.service.PushService;
import com.frankzhou.project.websocket.domain.dto.WebSocketRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 服务端推送实现
 * @date 2023-09-24
 */
@Slf4j
@Service
public class PushServiceImpl implements PushService {

    private static final Map<String, SseEmitter> SSE_MAP = new ConcurrentHashMap<>();

    @Override
    public SseEmitter getSseEmitter(@NotBlank(message = "客户端编号不能为空") String clientId) {
        final SseEmitter sseEmitter = SSE_MAP.get(clientId);

        if (ObjectUtil.isNotNull(sseEmitter)) {
            return sseEmitter;
        } else {
            // 建立连接
            final SseEmitter newSseEmitter = new SseEmitter(600000L);

            // 设置回调事件
            newSseEmitter.onCompletion(() -> {
                log.info("连接结束,即将关闭,clientId:{}",clientId);
                SSE_MAP.remove(clientId);
            });

            newSseEmitter.onTimeout(() -> {
                log.info("连接超时,即将关闭,clientId:{}",clientId);
                SSE_MAP.remove(clientId);
            });

            newSseEmitter.onError(throwable -> {
                log.info("连接错误,错误原因:{},即将关闭,clientId:{}",throwable.getMessage(),clientId);
                SSE_MAP.remove(clientId);
            });

            SSE_MAP.put(clientId,newSseEmitter);
            return newSseEmitter;
        }
    }

    @Override
    public void sendMessage(@NotBlank(message = "客户端编号不能为空") String clientId) throws IOException, InterruptedException {
        final SseEmitter sseEmitter = SSE_MAP.get(clientId);

        // 模拟推送JSON数据
        CatalogDTO catalog = new CatalogDTO();
        catalog.setCatalogName("规则目录");
        catalog.setParentId(1L);
        catalog.setLevel(1);
        catalog.setTreePath("/main");
        catalog.setSortNum(1);
        // 推送数据到客户端
        sseEmitter.send(catalog, MediaType.APPLICATION_JSON);
        log.info("推送数据:{}", JSONUtil.toJsonStr(catalog));
        Thread.sleep(2000);
        sseEmitter.send("此去经年，应是良辰好景虚设", MediaType.APPLICATION_JSON);
        log.info("推送数据:此去经年，应是良辰好景虚设");
        Thread.sleep(2000);
        sseEmitter.send("此去经年，应是良辰好景虚设，便纵有千种风情", MediaType.APPLICATION_JSON);
        log.info("推送数据:此去经年，应是良辰好景虚设，便纵有千种风情");
        Thread.sleep(2000);
        sseEmitter.send("此去经年，应是良辰好景虚设，便纵有千种风情，更与何人说", MediaType.APPLICATION_JSON);
        log.info("推送数据:此去经年，应是良辰好景虚设，便纵有千种风情，更与何人说");
        // 结束推送
        sseEmitter.complete();
    }

    @Override
    public void closeConnection(@NotBlank(message = "客户端编号不能为空") String clientId) {
        SseEmitter sseEmitter = SSE_MAP.get(clientId);
        if (ObjectUtil.isNotNull(sseEmitter)) {
            SSE_MAP.remove(clientId);
            log.info("连接已关闭");
        }
    }

    @Override
    public SseEmitter getMessage(@NotBlank(message = "客户端编号不能为空") String clientId) {
        SseEmitter sseEmitter = getSseEmitter(clientId);
        CompletableFuture.runAsync(() -> {
            try {
                sendMessage(clientId);
            } catch (Exception e) {
                throw new BusinessException("推送数据异常");
            }
        });

        return sseEmitter;
    }

    @Override
    public ResponseBodyEmitter pushCompleteRate(WebSocketRequestDTO requestDTO, HttpServletResponse response) {
        log.info("流式应答请求开始，请求信息：{}", JSONUtil.toJsonStr(requestDTO));
        try {
            // 设置响应头，流式输出，编码，禁用缓存
            response.setContentType("text/event-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Cache-Control", "no-cache");

            final ResponseBodyEmitter emitter = new ResponseBodyEmitter(3*60*100L);

            // 设置回调事件
            emitter.onCompletion(() -> {
                log.info("连接结束,即将关闭");
            });

            emitter.onTimeout(() -> {
                log.info("连接超时,即将关闭");
            });

            emitter.onError(throwable -> {
                log.info("连接错误,错误原因:{},即将关闭",throwable.getMessage());
            });

            CompletableFuture.runAsync(() -> {
                for (int i=0;i<10;i++) {
                    try {
                        Catalog catalog = new Catalog();
                        catalog.setCatalogName("目录"+i);
                        catalog.setSortNum(1);
                        emitter.send(catalog, MediaType.APPLICATION_JSON);
                        Thread.sleep(1000);
                        log.info("推送数据:{}",i);
                    } catch (Exception e) {
                        throw new BusinessException("流式应答出错");
                    }
                }

                emitter.complete();
            });

            return emitter;
        } catch (Exception e) {
            log.info("流式应答推送数据异常, 错误信息:{}", e.getMessage());
            throw new BusinessException("推送数据异常");
        }
    }
}
