package com.frankzhou.project.manager.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultCodeConstant;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.common.constant.OrderConstant;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.common.util.ListUtil;
import com.frankzhou.project.manager.UserManager;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.dto.user.UserAddDTO;
import com.frankzhou.project.model.dto.user.UserQueryDTO;
import com.frankzhou.project.model.dto.user.UserUpdateDTO;
import com.frankzhou.project.model.entity.User;
import com.frankzhou.project.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description service通用能力复用实现类(代码生成器)
 * @date 2023-05-02
 */
@Slf4j
@Component
public class UserManagerImpl implements UserManager {

    @Resource
    private UserMapper userMapper;

    @Override
    public ResultDTO<UserVO> getById(UserQueryDTO queryDTO) {
        if (ObjectUtil.isNull(queryDTO) || queryDTO.getId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getId,queryDTO.getId())
                .eq(User::getIsDelete,0);
        User oneById = userMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        UserVO userVO = new UserVO();
        BeanUtil.copyProperties(oneById,userVO);

        return ResultDTO.getSuccessResult(userVO);
    }

    @Override
    public ResultDTO<Boolean> updateById(UserUpdateDTO updateDTO) {
        if (ObjectUtil.isNull(updateDTO) || updateDTO.getId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getId,updateDTO.getId())
                .eq(User::getIsDelete,0);
        User oneById = userMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        User user = new User();
        BeanUtil.copyProperties(updateDTO,user);
        Integer updateCount = userMapper.updateById(user);
        if (updateCount < 1) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest) {
        if (ObjectUtil.isNull(deleteRequest) || deleteRequest.getId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<User>();
        wrapper.eq(User::getId,deleteRequest.getId()).eq(User::getIsDelete,0);
        User oneById = userMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        Integer deleteCount = userMapper.deleteById(deleteRequest);
        if (deleteCount < 1) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> insertOne(UserAddDTO addDTO) {
        if (ObjectUtil.isNull(addDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        User user = new User();
        BeanUtil.copyProperties(addDTO,user);
        Integer insertCount = userMapper.insert(user);
        if (insertCount < 1) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest) {
        if (ObjectUtil.isNull(deleteRequest) || StringUtils.isBlank(deleteRequest.getIds())) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String ids = deleteRequest.getIds();
        String[] idArray = ids.split(",");
        List<Long> idList = new ArrayList<>();
        for (String str : idArray) {
            idList.add(Long.valueOf(str));
        }

        Integer deleteCount = userMapper.batchDelete(idList);
        if (deleteCount != idList.size()) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_DELETE_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> batchInsert(List<UserAddDTO> addDTOList) {
        if (CollectionUtils.isEmpty(addDTOList)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        List<User> userList = ListUtil.listConvert(addDTOList, User.class);
        Integer insertCount = userMapper.batchInsert(userList);
        if (insertCount != userList.size()) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<Boolean> batchUpdate(List<UserUpdateDTO> updateDTOList) {
        if (CollectionUtils.isEmpty(updateDTOList)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        List<User> userList = ListUtil.listConvert(updateDTOList, User.class);
        Integer updateCount = userMapper.batchUpdate(userList);
        if (updateCount != userList.size()) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_UPDATE_COUNT_ERROR);
        }

        return ResultDTO.getSuccessResult(Boolean.TRUE);
    }

    @Override
    public ResultDTO<List<UserVO>> getListByCond(UserQueryDTO queryDTO) {
        if (ObjectUtil.isNull(queryDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        List<User> userList = userMapper.queryListByCond(queryDTO);
        List<UserVO> userVOList = new ArrayList<>();
        if (userList.size() == 0) {
            return ResultDTO.getSuccessResult(userVOList);
        }

        userVOList = ListUtil.listConvert(userList, UserVO.class);

        return ResultDTO.getSuccessResult(userVOList);
    }

    @Override
    public PageResultDTO<List<UserVO>> getPageListByCond(UserQueryDTO queryDTO) {
        if (ObjectUtil.isNull(queryDTO)) {
            return PageResultDTO.getErrorPageResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        Integer currPage = queryDTO.getCurrPage();
        Integer pageSize = queryDTO.getPageSize();
        String orderBy = queryDTO.getOrderBy();
        String sort = queryDTO.getSort();

        if (ObjectUtil.isNull(currPage)) {
            // 这里必须要替换成开始行数 startRow = (currPage-1)*pageSize
            queryDTO.setCurrPage(1);
        }
        if (ObjectUtil.isNull(pageSize)) {
            queryDTO.setPageSize(10);
        }
        if (StringUtils.isBlank(orderBy)) {
            queryDTO.setOrderBy("update_time");
        }
        if (StringUtils.isBlank(sort)) {
            queryDTO.setSort(OrderConstant.SORT_ORDER_ASC);
        }

        // 防止爬虫
        if (pageSize > 100) {
            throw new BusinessException(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        // 设置行数
        queryDTO.setStartRow((queryDTO.getCurrPage()-1)*queryDTO.getPageSize());
        Integer totalCount = userMapper.queryPageCount(queryDTO);
        List<UserVO> userVOList = new ArrayList<>();
        if (totalCount == 0) {
            return PageResultDTO.getSuccessPageResult(0,userVOList);
        }

        List<User> userList = userMapper.queryListByPage(queryDTO);
        userVOList = ListUtil.listConvert(userList, UserVO.class);

        return PageResultDTO.getSuccessPageResult(userList.size(),userVOList);
    }
}
