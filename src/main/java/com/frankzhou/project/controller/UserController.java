package com.frankzhou.project.controller;

import com.frankzhou.project.common.DeleteRequest;
import com.frankzhou.project.common.PageResultDTO;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.model.dto.user.*;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 用户管理前端控制器
 * @date 2023-04-08
 */
@Api(tags = {"用户管理"})
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    // ================用户登录注册==========================

    /**
    * @Author: FrankZhou
    * @Description: 获取验证码
    * @DateTime: 2023/4/9 0:35
    * @Params: phone 手机号码
    * @Return 验证码
    */
    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/sendCode")
    public ResultDTO<String> sendCode(@RequestParam String phone) {
        return ResultDTO.getSuccessResult();
    }

    /**
    * @Author: FrankZhou
    * @Description: 验证码登录
    * @DateTime: 2023/4/9 0:37
    * @Params: userLoginRequest 登录请求参数
    * @Return 用户信息
    */
    @ApiOperation(value = "验证码登录")
    @PostMapping("/code/login")
    public ResultDTO<UserVO> codeLogin(@RequestBody UserLoginRequest userLoginRequest) {
        return ResultDTO.getSuccessResult();
    }

    /**
    * @Author: FrankZhou
    * @Description: 用户注册
    * @DateTime: 2023/4/9 0:41
    * @Params: userRegisterRequest 用户注册参数
    * @Return 用户id
    */
    @ApiOperation(value = "用户注册")
    @PostMapping("/register")
    public ResultDTO<Long> register(@RequestBody UserRegisterRequest userRegisterRequest) {
        return ResultDTO.getSuccessResult();
    }

    /**
    * @Author: FrankZhou
    * @Description: 密码登录
    * @DateTime: 2023/4/9 0:42
    * @Params: userLoginRequest 用户登录参数
    * @Return 用户信息
    */
    @ApiOperation(value = "密码登录")
    @PostMapping("/password/login")
    public ResultDTO<UserVO> passwordLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest httpServletRequest) {
        return ResultDTO.getSuccessResult();
    }

    /**
    * @Author: FrankZhou
    * @Description: 用户登出
    * @DateTime: 2023/4/9 0:42
    * @Return true成功/false失败
    */
    @ApiOperation(value = "用户登出")
    @PostMapping("/logout")
    public ResultDTO<Boolean> logout(HttpServletRequest httpServletRequest) {
        return ResultDTO.getSuccessResult();
    }

    /**
    * @Author: FrankZhou
    * @Description: 获取当前登录用户
    * @DateTime: 2023/4/9 0:42
    * @Return 用户信息
    */
    @ApiOperation(value = "获取当前登录用户")
    @GetMapping("/get/login")
    public ResultDTO<UserVO> getLoginUser(HttpServletRequest httpServletRequest) {
        return ResultDTO.getSuccessResult();
    }

    // ================用户增删改查===========================

    @ApiOperation(value = "单条新增用户")
    @PostMapping("/add")
    public ResultDTO<Boolean> addUser(@RequestBody UserAddRequest addRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "单条删除用户")
    @PostMapping("/delete")
    public ResultDTO<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "单条更新用户")
    @PostMapping("/update")
    public ResultDTO<Boolean> updateUser(@RequestBody UserUpdateRequest updateRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "单条查询用户")
    @GetMapping("/get")
    public ResultDTO<UserVO> getUser(@RequestBody UserQueryRequest queryRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "批量查询用户")
    @GetMapping("/list")
    public ResultDTO<List<UserVO>> getUserList(@RequestBody UserQueryRequest queryRequest,HttpServletRequest httpServletRequest) {
        return ResultDTO.getSuccessResult();
    }

    @ApiOperation(value = "分页查询用户")
    @GetMapping("/list/page")
    public PageResultDTO<List<UserVO>> getUserByPage(@RequestBody UserQueryRequest queryRequest,HttpServletRequest httpServletRequest) {
        return PageResultDTO.getSuccessPageResult();
    }

    @ApiOperation(value = "用户下载")
    @GetMapping("/download")
    public void userDownload(@RequestBody UserQueryRequest queryRequest,HttpServletRequest httpServletRequest) {
    }

    @ApiOperation(value = "用户上传")
    @PostMapping("/upload")
    public ResultDTO<Boolean> userUpload(MultipartFile multipartFile) {
        return ResultDTO.getSuccessResult();
    }
}
