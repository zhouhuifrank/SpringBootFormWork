package com.frankzhou.project.service.impl;
import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.frankzhou.project.common.*;
import com.frankzhou.project.common.constant.UserRoleConstant;
import com.frankzhou.project.common.util.PasswordEncoder;
import com.frankzhou.project.common.util.RegexUtils;
import com.frankzhou.project.common.util.UserHolder;
import com.frankzhou.project.mapper.UserMapper;
import com.frankzhou.project.model.dto.user.*;
import com.frankzhou.project.model.entity.User;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.redis.RedisKeys;
import com.frankzhou.project.redis.RedisUtil;
import com.frankzhou.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        if (StringUtils.isBlank(phone) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PHONE_IS_INVALID);
        }

        // 生成验证码并存入redis
        String verifyCode = RandomUtil.randomNumbers(6);
        redisUtil.setCacheString(RedisKeys.LOGIN_CODE_KEY+phone,verifyCode,RedisKeys.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.info("sendCode: 验证码发送成功:{}",verifyCode);

        return ResultDTO.getSuccessResult(verifyCode);
    }

    @Override
    public ResultDTO<UserDTO> userCodeLogin(UserLoginRequest loginRequest) {
        if (ObjectUtil.isNull(loginRequest)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String phone = loginRequest.getPhone();
        String code = loginRequest.getCode();
        if (StringUtils.isBlank(phone) || RegexUtils.phoneIsInvalid(phone)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PHONE_IS_INVALID);
        }

        // 校验验证码
        String cacheCode = (String) redisUtil.getCacheString(RedisKeys.LOGIN_CODE_KEY + phone);
        if (StringUtils.isBlank(cacheCode) || !cacheCode.equals(code)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.CODE_IS_ERROR);
        }

        // 根据电话查找用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone)
                .eq(User::getIsDelete,0);
        User user = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(user)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_REGISTER);
        }

        // 生成token存入redis中
        String token = UUID.fastUUID().toString();
        UserDTO loginUser = new UserDTO();
        loginUser.setId(user.getId());
        loginUser.setUserName(user.getUserName());
        loginUser.setRole(user.getUserRole());
        loginUser.setPhone(user.getPhone());
        String loginUserKey = RedisKeys.LOGIN_USER_KEY + token;
        // 对象转map
        Map<String, Object> loginUserMap = BeanUtil.beanToMap(loginUser, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true));
        redisUtil.setCacheHash(loginUserKey,loginUserMap);
        redisUtil.setExpire(loginUserKey,RedisKeys.LOGIN_USER_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(loginUser);
    }

    @Override
    public ResultDTO<Long> userRegister(UserRegisterRequest registerRequest) {
        if (ObjectUtil.isNull(registerRequest)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String confirmPassword = registerRequest.getConfirmPassword();
        String phone = registerRequest.getPhone();
        String userRole = registerRequest.getUserRole();
        if (StringUtils.isAnyBlank(userAccount,userPassword,confirmPassword)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        if (StringUtils.isNotBlank(phone)) {
            if (RegexUtils.phoneIsInvalid(phone)) {
                return ResultDTO.getErrorResult(ResultCodeConstant.PHONE_IS_INVALID);
            }
        }

        if (StringUtils.isBlank(userRole)) {
            userRole = UserRoleConstant.USER_ROLE;
        }

        // 校验用户是否已经注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User dbUser = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNotNull(dbUser)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_HAS_EXISTED);
        }

        // 校验密码是否一致
        if (!userPassword.equals(confirmPassword) || RegexUtils.passwordIsInvalid(userPassword)|| RegexUtils.passwordIsInvalid(confirmPassword)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PASSWORD_ERROR);
        }

        // 密码加密
        String encodePassword = PasswordEncoder.encode(userPassword);
        User registerUser = new User();
        registerUser.setUserAccount(userAccount);
        if (StringUtils.isBlank(phone)) {
            registerUser.setPhone(phone);
        }
        registerUser.setUserRole(userRole);
        registerUser.setUserPassword(encodePassword);
        Integer insertCount = userMapper.insert(registerUser);
        if (insertCount < 1) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_INSERT_COUNT_ERROR);
        }

        Long userId = registerUser.getId();
        return ResultDTO.getSuccessResult(userId);
    }

    @Override
    public ResultDTO<UserDTO> userPasswordLogin(UserLoginRequest loginRequest) {
        if (ObjectUtil.isNull(loginRequest)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String password = loginRequest.getUserPassword();
        String account = loginRequest.getUserAccount();
        if (StringUtils.isBlank(account)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        if (StringUtils.isBlank(password) || RegexUtils.passwordIsInvalid(password)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PHONE_IS_INVALID);
        }

        // 根据账号查找密码
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,account)
                .eq(User::getIsDelete,0);
        User dbUser = userMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(dbUser)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.USER_NOT_REGISTER);
        }

        // 校验密码是否相同
        String encodePassword = dbUser.getUserPassword();
        if (!PasswordEncoder.isMatch(password,encodePassword)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PASSWORD_ERROR);
        }

        // 生成token存入redis中
        String token = UUID.fastUUID().toString();
        UserDTO loginUser = new UserDTO();
        loginUser.setId(loginUser.getId());
        loginUser.setUserName(loginUser.getUserName());
        loginUser.setRole(loginUser.getRole());
        loginUser.setPhone(loginUser.getPhone());
        String loginUserKey = RedisKeys.LOGIN_USER_KEY + token;
        // 对象转map
        Map<String, Object> loginUserMap = BeanUtil.beanToMap(loginUser, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true));
        redisUtil.setCacheHash(loginUserKey,loginUserMap);
        redisUtil.setExpire(loginUserKey,RedisKeys.LOGIN_USER_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(loginUser);
    }

    @Override
    public ResultDTO<UserDTO> getLoginUser() {
        // 从ThreadLocal中拿到当前登录的用户
        UserDTO user = UserHolder.getUser();
        return ResultDTO.getSuccessResult(user);
    }

    @Override
    public ResultDTO<Boolean> userLogout(HttpServletRequest httpServletRequest) {
        // 注销只需要将redis中的用户信息删除即可
        String token = httpServletRequest.getHeader("authorization");
        if (StringUtils.isBlank(token)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.TOKEN_NOT_EXISTED);
        }
        String loginUser = RedisKeys.LOGIN_USER_KEY + token;
        boolean flag = redisUtil.deleteObject(loginUser);
        return ResultDTO.getSuccessResult(BooleanUtil.isTrue(flag));
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
