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

    MALE("male","男"),
    FEMALE("female","女");

    private final String value;

    private final String desc;

    PostGenderStatusEnum(String value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static List<String> getValues() {
        List<String> genderValues = Arrays.stream(values()).map(PostGenderStatusEnum::getValue).collect(Collectors.toList());
        return genderValues;
    }

    public static PostGenderStatusEnum getEnumByValue(String value) {
        PostGenderStatusEnum[] values = values();
        for (PostGenderStatusEnum status : values) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }

        return null;
    }
}
