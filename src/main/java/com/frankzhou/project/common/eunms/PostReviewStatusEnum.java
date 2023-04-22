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

    VERIFY_BEFORE(0,"待审核"),
    VERIFY_PASS(1,"审核通过"),
    VERIFY_FAIL(2,"审核失败");

    private final Integer code;

    private final String desc;

    PostReviewStatusEnum(Integer code,String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static List<Integer> getValues() {
        List<Integer> statusValues = Arrays.stream(values()).map(PostReviewStatusEnum::getCode).collect(Collectors.toList());
        return statusValues;
    }
}
