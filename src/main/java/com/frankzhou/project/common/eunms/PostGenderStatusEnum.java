package com.frankzhou.project.common.eunms;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子用户性别枚举
 * @date 2023-04-22
 */
@Getter
public enum PostGenderStatusEnum {

    MALE(0,"男"),
    FEMALE(1,"女");

    private final Integer code;

    private final String desc;

    PostGenderStatusEnum(Integer code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<Integer> getValues() {
        List<Integer> genderValues = Arrays.stream(values()).map(PostGenderStatusEnum::getCode).collect(Collectors.toList());
        return genderValues;
    }
}
