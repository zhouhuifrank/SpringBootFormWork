package com.frankzhou.project.service;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.dto.user.*;
import com.frankzhou.project.model.vo.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理业务逻辑层接口
 * @date 2023-04-08
 */
public interface UserService {

    // ===============前台方法======================

    ResultDTO<String> sendCode(String phone);

    ResultDTO<UserVO> userCodeLogin(UserLoginRequest loginRequest);

    ResultDTO<Long> userRegister(UserRegisterRequest registerRequest);

    ResultDTO<UserVO> userPasswordLogin(UserLoginRequest loginRequest);

    ResultDTO<UserVO> getLoginUser(HttpServletRequest httpServletRequest);

    ResultDTO<Boolean> userLogout(HttpServletRequest httpServletRequest);

    // ===============后台方法=======================

    ResultDTO<Boolean> updateById(UserUpdateRequest updateRequest);

    ResultDTO<UserVO> getById(UserQueryRequest queryRequest);

    ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest);

    ResultDTO<Boolean> insertOne(UserAddRequest addRequest);

    ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest);

    ResultDTO<List<UserVO>> getListByCond(UserQueryRequest queryRequest);

    PageResultDTO<List<UserVO>> getPageListByCond(UserQueryRequest queryRequest);

    void userDownload(UserQueryRequest queryRequest, HttpServletResponse httpServletResponse);

    ResultDTO<Boolean> userUpload(MultipartFile multipartFile);
}
