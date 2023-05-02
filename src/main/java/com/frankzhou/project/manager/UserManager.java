package com.frankzhou.project.manager;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.user.UserAddDTO;
import com.frankzhou.project.model.dto.user.UserQueryDTO;
import com.frankzhou.project.model.dto.user.UserUpdateDTO;
import com.frankzhou.project.model.vo.UserVO;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description service通用能力复用
 * @date 2023-05-02
 */
public interface UserManager {

    ResultDTO<UserVO> getById(UserQueryDTO queryDTO);

    ResultDTO<Boolean> updateById(UserUpdateDTO updateDTO);

    ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest);

    ResultDTO<Boolean> insertOne(UserAddDTO addDTO);

    ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest);

    ResultDTO<Boolean> batchInsert(List<UserAddDTO> addDTOList);

    ResultDTO<Boolean> batchUpdate(List<UserUpdateDTO> updateDTOList);

    ResultDTO<List<UserVO>> getListByCond(UserQueryDTO queryDTO);

    PageResultDTO<List<UserVO>> getPageListByCond(UserQueryDTO queryDTO);
}
