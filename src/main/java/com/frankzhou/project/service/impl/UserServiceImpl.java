package com.frankzhou.project.service.impl;

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
        redisUtil.setCacheString(RedisKeys.LOGIN_CODE_KEY + phone, verifyCode, RedisKeys.LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.info("sendCode: 验证码发送成功:{}", verifyCode);

        return ResultDTO.getSuccessResult(verifyCode);
    }

    @Override
    public ResultDTO<UserVO> userCodeLogin(UserLoginDTO loginDTO) {
        if (ObjectUtil.isNull(loginDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String phone = loginDTO.getPhone();
        String code = loginDTO.getCode();
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
        UserVO loginUser = new UserVO();
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
    public ResultDTO<Long> userRegister(UserRegisterDTO registerDTO) {
        if (ObjectUtil.isNull(registerDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String userAccount = registerDTO.getUserAccount();
        String userPassword = registerDTO.getUserPassword();
        String confirmPassword = registerDTO.getConfirmPassword();
        String phone = registerDTO.getPhone();
        String userRole = registerDTO.getUserRole();
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
    public ResultDTO<UserVO> userPasswordLogin(UserLoginDTO loginDTO) {
        if (ObjectUtil.isNull(loginDTO)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        String password = loginDTO.getUserPassword();
        String account = loginDTO.getUserAccount();
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
        UserVO loginUser = new UserVO();
        loginUser.setId(dbUser.getId());
        loginUser.setUserName(dbUser.getUserName());
        loginUser.setRole(dbUser.getUserRole());
        loginUser.setPhone(dbUser.getPhone());
        String loginUserKey = RedisKeys.LOGIN_USER_KEY + token;
        // 对象转map
        Map<String, Object> loginUserMap = BeanUtil.beanToMap(loginUser, new HashMap<>(),
                CopyOptions.create().setIgnoreNullValue(true));
        redisUtil.setCacheHash(loginUserKey,loginUserMap);
        redisUtil.setExpire(loginUserKey,RedisKeys.LOGIN_USER_TTL,TimeUnit.MINUTES);

        return ResultDTO.getSuccessResult(loginUser);
    }

    @Override
    public ResultDTO<UserVO> getLoginUser() {
        // 从ThreadLocal中拿到当前登录的用户
        UserVO user = UserHolder.getUser();
        return ResultDTO.getSuccessResult(user);
    }

    @Override
    public ResultDTO<Boolean> userLogout(HttpServletRequest request) {
        // 注销只需要将redis中的用户信息删除即可
        String token = request.getHeader("authorization");
        if (StringUtils.isBlank(token)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.TOKEN_NOT_EXISTED);
        }
        String loginUser = RedisKeys.LOGIN_USER_KEY + token;
        boolean flag = redisUtil.deleteObject(loginUser);
        return ResultDTO.getSuccessResult(BooleanUtil.isTrue(flag));
    }

    @Override
    public ResultDTO<Boolean> updateById(UserUpdateDTO updateDTO) {
        if (ObjectUtil.isNull(updateDTO) || updateDTO.getId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,updateDTO.getId())
                .eq(User::getIsDelete,0);
        User oneById = userMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        // 校验参数
        if (StringUtils.isBlank(updateDTO.getPhone()) || RegexUtils.phoneIsInvalid(updateDTO.getPhone())) {
            return ResultDTO.getErrorResult(ResultCodeConstant.PHONE_IS_INVALID);
        }

        if (StringUtils.isNotBlank(updateDTO.getUserRole())) {

        }

        User user = new User();
        // userAccount不能修改
        user.setUserAccount(oneById.getUserAccount());


        return null;
    }

    @Override
    public ResultDTO<UserVO> getById(UserQueryDTO queryDTO) {
        if (ObjectUtil.isNull(queryDTO) || queryDTO.getId() <= 0) {
            return ResultDTO.getErrorResult(ResultCodeConstant.REQUEST_PARAM_ERROR);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId,queryDTO.getId())
                .eq(User::getIsDelete,0);
        User oneById = userMapper.selectOne(wrapper);
        if (ObjectUtil.isNull(oneById)) {
            return ResultDTO.getErrorResult(ResultCodeConstant.DB_QUERY_NO_DATA);
        }

        UserVO userVO = new UserVO();
        userVO.setId(oneById.getId());
        userVO.setUserName(oneById.getUserName());
        userVO.setPhone(oneById.getPhone());
        userVO.setRole(oneById.getUserRole());

        return ResultDTO.getSuccessResult(userVO);
    }

    @Override
    public ResultDTO<Boolean> deleteById(DeleteRequest deleteRequest) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> insertOne(UserAddDTO addDTO) {
        return null;
    }

    @Override
    public ResultDTO<Boolean> batchDelete(DeleteRequest deleteRequest) {
        return null;
    }

    @Override
    public ResultDTO<List<UserVO>> getListByCond(UserQueryDTO queryDTO) {
        return null;
    }

    @Override
    public PageResultDTO<List<UserVO>> getPageListByCond(UserQueryDTO queryDTO) {
        return null;
    }

    @Override
    public void userDownload(UserQueryDTO queryDTO, HttpServletResponse response) {

    }

    @Override
    public ResultDTO<Boolean> userUpload(MultipartFile multipartFile) {
        return null;
    }

}
