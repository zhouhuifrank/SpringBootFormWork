package com.frankzhou.project.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description EasyExcel测试类
 * @date 2023-07-04
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestExcel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String problemNo;

    private String level;

    private String problemTitle;

    private String createdUser;

    private Date createdTime;

    private String problemDesc;

    private String dutyOperNo;
}
