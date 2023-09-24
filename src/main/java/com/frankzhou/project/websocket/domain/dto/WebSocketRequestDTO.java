package com.frankzhou.project.websocket.domain.dto;

import lombok.Data;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket请求体
 * @date 2023-09-24
 */
@Data
public class WebSocketRequestDTO {
    /**
     * 请求类型
     * @see com.frankzhou.project.websocket.domain.enums.WebSocketRequestTypeEnum
     */
    Integer type;

    /**
     * 前端传过来的枚举类
     */
    String data;
}
