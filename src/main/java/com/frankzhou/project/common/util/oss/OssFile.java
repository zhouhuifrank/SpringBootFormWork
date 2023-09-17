package com.frankzhou.project.common.util.oss;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Oss文件信息
 * @date 2023-07-21
 */
@Data
@AllArgsConstructor
@Builder
public class OssFile {

    /**
     * Oss存储时文件路径
     */
    private String filePath;

    /**
     * 原始文件名
     */
    private String originalFileName;
}
