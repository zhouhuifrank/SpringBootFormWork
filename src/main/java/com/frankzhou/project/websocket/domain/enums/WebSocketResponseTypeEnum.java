package com.frankzhou.project.websocket.domain.enums;

import com.frankzhou.project.common.ResultDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket响应类型类聚
 * @date 2023-09-24
 */
@Getter
@AllArgsConstructor
public enum WebSocketResponseTypeEnum {

    LOGIN_SUCCESS(1,"登录成功，返回用户信息", ResultDTO.class),
    ONLINE_OFFLINE_NOTIFY(2,"上下线通知", ResultDTO.class),
    MESSAGE(3,"消息",ResultDTO.class),
    INVALIDATE_TOKEN(4,"前端token失效",null),
    TASK_RATE(5,"任务进度推送",ResultDTO.class),
    EXECUTE_RESULT(6,"执行结果推送",ResultDTO.class);

    private Integer code;

    private String desc;

    private Class dataClass;

    private static Map<Integer,WebSocketResponseTypeEnum> enumCache;

    static {
        enumCache = Arrays.stream(WebSocketResponseTypeEnum.values()).collect(Collectors.toMap(WebSocketResponseTypeEnum::getCode, dto -> dto));
    }

    public static WebSocketResponseTypeEnum getEnumByCode(Integer code) {
        return enumCache.get(code);
    }
}
