package com.frankzhou.project.common;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分页查找请求类
 * @date 2023-04-08
 */
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer currPage;

    private Integer pageSize;

    private String orderBy;

    private String sort;
}
