package com.frankzhou.project.common.util;

import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表转换工具类
 */
public class ListUtil<T> {

    /**
     * 将单个对象转换成列表
     */
    public static <T> List<T> singletonList(T item) {
        List<T> resList = new ArrayList<>();
        resList.add(item);
        return resList;
    }

    /**
     * 列表类型转换-->用于DO、DTO、VO的转换
     *
     */
    public static <E, T> List<T> listConvert(List<E> source,Class<T> clazz) {
        if (CollectionUtils.isEmpty(source)) {
            return new ArrayList<>();
        }

        List<T> resList = new ArrayList<>(source.size());
        source.forEach(s -> {
            T t = BeanUtils.instantiateClass(clazz);
            BeanUtils.copyProperties(s,t);
            resList.add(t);
        });

        return resList;
    }

}
