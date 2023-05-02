package com.frankzhou.project.common.eunms;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 权限枚举类
 * @date 2023-05-02
 */
@Getter
public enum UserRoleEnum {

    ADMIN_ROLE("admin","管理员"),
    USER_ROLE("user","普通用户"),
    PROHIBIT_ROLE("prohibit","被封号");

    private final String value;

    private final String desc;

    UserRoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static List<String> getValues() {
        List<String> roleList = Arrays.stream(values()).map(UserRoleEnum::getValue).collect(Collectors.toList());
        return roleList;
    }

    public static UserRoleEnum getEnumByValue(String value) {
        UserRoleEnum[] values = values();
        for (UserRoleEnum role : values) {
            if (role.getValue().equals(value)) {
                return role;
            }
        }

        return null;
    }
}
