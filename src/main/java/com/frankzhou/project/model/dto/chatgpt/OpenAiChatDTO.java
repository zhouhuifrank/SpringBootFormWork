package com.frankzhou.project.model.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-12-10
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiChatDTO implements Serializable {
    private static final long serialVersionUID = 13457L;

    /**
     * 用户id 输入信息的用户 用于权限、api使用次数计费等功能
     */
    Long userId;

    /**
     * 模型类型  openAi/qianwen/fdu/tencent/xunfei
     */
    String modelType;

    /**
     * 模型编号 模型类型对应的模型编号 比如
     */
    String modelCode;

    List<ChatMessage> messageList;
}
