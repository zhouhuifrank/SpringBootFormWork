package com.frankzhou.project.service;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
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

    ResultDTO<UserVO> userCodeLogin(UserLoginDTO loginDTO);

    ResultDTO<Long> userRegister(UserRegisterDTO registerDTO);

    ResultDTO<UserVO> userPasswordLogin(UserLoginDTO loginDTO);

    ResultDTO<UserVO> getLoginUser();

    ResultDTO<Boolean> userLogout(HttpServletRequest request);

    // ===============后台方法=======================

    ResultDTO<Boolean> updateById(UserUpdateDTO updateDTO);

    ResultDTO<UserVO> getById(UserQueryDTO queryDTO);

    ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest);

    ResultDTO<Boolean> insertOne(UserAddDTO addDTO);

    ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest);

    ResultDTO<List<UserVO>> getListByCond(UserQueryDTO queryDTO);

    PageResultDTO<List<UserVO>> getPageListByCond(UserQueryDTO queryDTO);

    void userDownload(UserQueryDTO queryDTO, HttpServletResponse response);

    ResultDTO<Boolean> userUpload(MultipartFile multipartFile);
}
