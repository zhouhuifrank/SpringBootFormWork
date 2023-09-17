package com.frankzhou.project.model.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 接口限流请求入参对象
 * @date 2023-08-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FrequencyLimitDTO {

    private String key;

    private long time;

    private TimeUnit unit;

    private Integer count;
}
