package com.frankzhou.project.mapper;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description
 * @date 2023-09-16
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.project.model.entity.Catalog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CatalogMapper extends BaseMapper<Catalog> {

    Integer batchInsert(@Param("list") List<Catalog> catalogList);

    Integer batchUpdate(@Param("list") List<Catalog> catalogList);
}
