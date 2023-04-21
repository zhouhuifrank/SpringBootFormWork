package com.frankzhou.project.common;

import java.io.Serializable;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 分页查找请求类 需要分页查找时继承即可
 * @date 2023-04-08
 */
public class PageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页数
     */
    private Integer currPage;

    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 排序字段
     */
    private String orderBy;

    /**
     * 顺序 asc升序/desc降序
     */
    private String sort;
}
