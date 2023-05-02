package com.frankzhou.project.common.util;

import com.frankzhou.project.common.ResultCodeConstant;
import com.frankzhou.project.common.ResultCodeDTO;
import com.frankzhou.project.common.exception.BusinessException;
import com.frankzhou.project.config.AudienceConfig;
import com.frankzhou.project.model.entity.User;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author This.FrankZhou
 * @version 1.0
 * @description Jwt生成Token
 * @date 2023-04-22
 */
@Slf4j
public class JwtTokenUtil {

    public static final String AUTH_HEADER_KEY = "Authorization";

    public static final String TOKEN_PREFIX = "Bearer ";

    public static final Integer EXPIRE_MIN = 30;

    /**
    * @Author: FrankZhou
    * @Description: 生成token
    * @DateTime: 2023/5/2 14:09
    * @Params: user 用户信息
    * @Return token
    */
    public static String createToken(User user, AudienceConfig audience) {
        try {
            // 使用HS256加密算法
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            byte[] apiKeySecret = DatatypeConverter.parseBase64Binary(audience.getBase64Secret());
            Key signKey = new SecretKeySpec(apiKeySecret,signatureAlgorithm.getJcaName());
            // 生成签发时间和过期时间
            Date issueDate = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(issueDate);
            calendar.add(Calendar.MINUTE,EXPIRE_MIN);
            Date expireDate = calendar.getTime();
            JwtBuilder jwtBuilder = Jwts.builder()
                    .setHeaderParam("type", "JWT")
                    .setId(String.valueOf(user.getId()))
                    // subject代表JWT的主体，即这个token的所有人
                    .setSubject(user.getUserName())
                    // issuer代表JWT的签发主体
                    .setIssuer(audience.getClientId())
                    // 时间戳，代表JWT的签发时间
                    .setIssuedAt(issueDate)
                    // 代表这个JWT的接受对象
                    .setAudience(audience.getName())
                    //设置签发密钥
                    .signWith(SignatureAlgorithm.HS256,signKey)
                    // 设置过期时间
                    .setExpiration(expireDate);
            String token = TOKEN_PREFIX + jwtBuilder.compact();
            return token;
        } catch (Exception e) {
            log.error("token sign error: {}",e.getMessage());
            throw new BusinessException(ResultCodeConstant.TOKEN_GEN_FAIL);
        }
    }

    public static Claims parseToken(String token,String base64Secret) {
        try {
            String[] str = token.split(" ");
            String realToken = str[1];
            Claims claims = Jwts.parser()
                    .setSigningKey(DatatypeConverter.parseBase64Binary(base64Secret))
                    .parseClaimsJws(realToken)
                    .getBody();
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("token expire: {}",e.getMessage());
            throw new BusinessException(ResultCodeConstant.TOKEN_IS_EXPIRED);
        } catch (Exception e) {
            log.error("token invalid: {}",e.getMessage());
            throw new BusinessException(ResultCodeConstant.TOKEN_NOT_EXISTED);
        }
    }

    public static String getUserName(String token,AudienceConfig audience) {
        return parseToken(token,audience.getBase64Secret()).getSubject();
    }

    public static Long getUserId(String token,AudienceConfig audience) {
        String userId = parseToken(token,audience.getBase64Secret()).getId();
        return Long.valueOf(userId);
    }

    public static boolean isExpire(String token,AudienceConfig audience) {
        return parseToken(token,audience.getBase64Secret()).getExpiration().before(new Date());
    }
}
