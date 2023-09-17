package com.frankzhou.project.controller;

import com.frankzhou.project.annotation.FrequencyLimit;
import com.frankzhou.project.annotation.RedissonLock;
import com.frankzhou.project.annotation.RedissonLockV2;
import com.frankzhou.project.annotation.RepeatSubmit;
import com.frankzhou.project.common.ResultDTO;
import com.frankzhou.project.manager.UserManager;
import com.frankzhou.project.model.dto.user.UserAddDTO;
import com.frankzhou.project.model.dto.user.UserQueryDTO;
import com.frankzhou.project.model.vo.UserVO;
import com.frankzhou.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description 测试请求
 * @date 2023-06-09
 */
@Slf4j
@RestController
@Api(tags = {"测试"})
@RequestMapping("/test")
public class TestController {

    @Resource
    private UserService userService;
    @Resource
    private UserManager userManager;

    @RedissonLock(prefixKey = "frankzhou:test",key = "success",waitTime = 1L,unit = TimeUnit.MINUTES)
    @ApiOperation(value = "【测试】分布式锁注解", notes = "RedissonLock")
    @GetMapping("/lockAnnotation")
    public ResultDTO<String> sendCode(String phone) {
        return userService.sendCode(phone);
    }

    @RedissonLockV2(prefixKey = "frankzhou:test2",key = "#phone",waitTime = 1L,unit = TimeUnit.MINUTES)
    @ApiOperation(value = "【测试】分布式锁注解spEl版本", notes = "RedissonLockV2")
    @GetMapping("/spElLock")
    public ResultDTO<String> sendCodeV2(String phone) {
        return userService.sendCode(phone);
    }

    @RepeatSubmit(target = RepeatSubmit.Target.URL,interval = 1L,unit = TimeUnit.MINUTES)
    @ApiOperation(value = "【测试】防止重复提交表单", notes = "repeatSubmit")
    @PostMapping("/repeatSubmit")
    public ResultDTO<Boolean> addUser(@RequestBody UserAddDTO addDTO) {
        return userManager.insertOne(addDTO);
    }

    @FrequencyLimit(target = FrequencyLimit.Target.EL,spEl = "#phone",time = 2,unit = TimeUnit.MINUTES, count = 15)
    @FrequencyLimit(target = FrequencyLimit.Target.EL,spEl = "#phone",time = 1,unit = TimeUnit.MINUTES, count = 10)
    @ApiOperation(value = "【测试】频率控制注解", notes = "FrequencyLimit")
    @GetMapping("/frequencyLimit")
    public ResultDTO<String> sendCodeV3(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }
}
