package com.frankzhou.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.frankzhou.project.model.dto.user.UserQueryDTO;
import com.frankzhou.project.model.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理数据处理层(代码生成器)
 * @date 2023-04-08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> queryListByCond(UserQueryDTO queryDTO);

    List<User> queryListByPage(UserQueryDTO queryDTO);

    Integer queryPageCount(UserQueryDTO queryDTO);

    Integer batchInsert(@Param("list") List<User> userList);

    Integer batchUpdate(@Param("list") List<User> userList);

    Integer batchDelete(@Param("ids") List<Long> idList);
}
