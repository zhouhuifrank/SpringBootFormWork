package com.frankzhou.project.service.impl;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.dto.user.*;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.redis.RedisUtil;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理业务逻辑层
 * @date 2023-04-08
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public ResultDTO<String> sendCode(String phone) {
        return null;
    }

    @Override
    public ResultDTO<UserVO> userCodeLogin(UserLoginRequest loginRequest) {
        return null;
    }

    @Override
    public ResultDTO<Long> userRegister(UserRegisterRequest registerRequest) {
        return null;
    }

    @Override
    public ResultDTO<UserVO> userPasswordLogin(UserLoginRequest loginRequest) {
        return null;
    }

    @Override
    public ResultDTO<UserVO> getLoginUser(HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> userLogout(HttpServletRequest httpServletRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> updateById(UserUpdateRequest updateRequest) {
        return null;
    }

    @Override
    public ResultDTO<UserVO> getById(UserQueryRequest queryRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> insertOne(UserAddRequest addRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest) {
        return null;
    }

    @Override
    public ResultDTO<List<UserVO>> getListByCond(UserQueryRequest queryRequest) {
        return null;
    }

    @Override
    public PageResultDTO<List<UserVO>> getPageListByCond(UserQueryRequest queryRequest) {
        return null;
    }

    @Override
    public void userDownload(UserQueryRequest queryRequest, HttpServletResponse httpServletResponse) {

    }

    @Override
    public ResultDTO<Boolean> userUpload(MultipartFile multipartFile) {
        return null;
    }
}
