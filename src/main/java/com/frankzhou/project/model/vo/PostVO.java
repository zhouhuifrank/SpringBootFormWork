package com.frankzhou.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-04-08
 */
@Data
public class PostVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private Integer age;

    private Integer gender;

    private String education;

    private String place;

    private String job;

    private String contact;

    private String loveExperience;

    private String content;

    private String photo;

    private Integer reviewStatus;

    private String reviewMessage;

    private Integer viewNum;

    private Integer collectNum;

    private Integer thumbNum;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private Boolean hashCollect;

    private Boolean hasThumb;
}
