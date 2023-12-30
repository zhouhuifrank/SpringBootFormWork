package com.frankzhou.project.Util;

import com.frankzhou.project.common.util.JwtTokenUtil;
import com.frankzhou.project.common.util.JwtUtil;
import com.frankzhou.project.config.AudienceConfig;
import com.frankzhou.project.model.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description jwt token测试
 * @date 2023-05-02
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class TokenUtilTests {

    @Resource
    AudienceConfig audience;

    @Resource
    private JwtUtil jwtUtil;

    @Test
    public void testCreateToken() {
        User user = new User();
        user.setId(1L);
        user.setUserName("frankzhou");
        String token = JwtTokenUtil.createToken(user, audience);

        System.out.println(token);
        System.out.println(JwtTokenUtil.getUserName(token,audience));
        System.out.println(JwtTokenUtil.getUserId(token,audience));
    }

    @Test
    public void testJwt() {
        JwtUtil jwtUtil = new JwtUtil();
        String token = jwtUtil.createToken(1234L);
        log.info("token:{}",token);
        Long userId = jwtUtil.getUserId(token);
        log.info("parse token userId:{}",userId);
    }
}
