package com.frankzhou.project.websocket.domain.vo;

import lombok.Data;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description WebSocket基础返回对象
 * @date 2023-09-24
 */
@Data
public class WebSocketBaseVO<T> {
    /**
     * 响应类型
     * @see com.frankzhou.project.websocket.domain.enums.WebSocketResponseTypeEnum
     */
    Integer type;

    /**
     * 泛型，根据type进行转化
     */
    T data;
}
