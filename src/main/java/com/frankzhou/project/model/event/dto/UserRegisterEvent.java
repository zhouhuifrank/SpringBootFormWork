package com.frankzhou.project.model.event.dto;

import com.frankzhou.project.model.entity.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户注册事件
 * @date 2023-08-19
 */
@Getter
public class UserRegisterEvent extends ApplicationEvent {

    private User user;

    public UserRegisterEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
