package com.frankzhou.project.model.dto.chatgpt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-12-11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessage {

    private String role;

    private String content;
}
