package com.frankzhou.project.common.eunms;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 帖子审核状态枚举类
 * @date 2023-04-22
 */
@Getter
public enum PostReviewStatusEnum {

    VERIFY_BEFORE("CHECK_BEFORE","待审核"),
    VERIFY_PASS("CHECK_SUCCESS","审核通过"),
    VERIFY_FAIL("CHECK_FAIL","审核失败");

    private final String value;

    private final String desc;

    PostReviewStatusEnum(String value,String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static List<String> getValues() {
        List<String> statusValues = Arrays.stream(values()).map(PostReviewStatusEnum::getValue).collect(Collectors.toList());
        return statusValues;
    }

    public static PostReviewStatusEnum getEnumByValue(String value) {
        PostReviewStatusEnum[] values = values();
        for (PostReviewStatusEnum status : values) {
            if (status.getValue().equals(value)) {
                return status;
            }
        }

        return null;
    }
}
