package com.frankzhou.project.websocket.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.common.message.DescribeProducersRequestData;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket请求类型枚举
 * @date 2023-09-24
 */
@Getter
@AllArgsConstructor
public enum WebSocketRequestTypeEnum {

    LOGIN(1,"登录认证"),
    HEART(2,"发送心跳"),
    EXECUTE_TASK(3,"执行任务");

    private Integer code;

    private String desc;

    private static Map<Integer,WebSocketRequestTypeEnum> enumCache;

    static {
        enumCache = Arrays.stream(WebSocketRequestTypeEnum.values()).collect(Collectors.toMap(WebSocketRequestTypeEnum::getCode, dto -> dto));
    }

    public static WebSocketRequestTypeEnum getEnumByCode(Integer code) {
        return enumCache.get(code);
    }
}
