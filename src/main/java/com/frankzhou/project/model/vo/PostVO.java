package com.frankzhou.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import java.util.List;

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

    private String title;

    private String content;

    private List<String> tags;

    private Integer visitNum;

    private Integer favourNum;

    private Integer thumbNum;

    private Long userId;

    private Date createTime;

    private Date updateTime;

    private UserVO user;

    private Boolean hashFavour;

    private Boolean hasThumb;
}
