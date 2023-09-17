package com.frankzhou.project.common.util.oss;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 对象存储类型
 * @date 2023-07-21
 */
@Getter
@AllArgsConstructor
public enum OssType {

    MINIO("minio",1),
    OBS("huawei",2),
    COS("tencent",3),
    ALIBABA("alibaba",4);

    private String name;

    private Integer type;
}
