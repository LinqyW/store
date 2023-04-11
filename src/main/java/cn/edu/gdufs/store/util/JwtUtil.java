package cn.edu.gdufs.store.util;

import io.jsonwebtoken.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.UUID;

/**
 * Description:token工具类
 */
@Component
public class JwtUtil {
    //过期时间(半小时)
    private static long tokenExpiration = 30*60*1000;
    //签名秘钥
    private static String tokenSignKey = "secret";

    //根据参数生成token，三部分组成，用 . 隔开
    public static String createToken(Integer uid, String username) {
        String token = Jwts.builder()
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration)) // 公共部分结束
                .claim("jti", UUID.randomUUID().toString())
                .claim("userId", uid)
                .claim("userName", username) // 私有部分，实际上真正需要封装的信息（id和name）
                .signWith(SignatureAlgorithm.HS512, new SecretKeySpec(tokenSignKey.getBytes(), SignatureAlgorithm.HS512.getJcaName())) // 签名部分
                .compressWith(CompressionCodecs.DEFLATE)
                .compact();
        return token;
    }

    //根据token字符串得到用户id
    public static Integer getUid(String token) {
        if(StringUtils.isEmpty(token)) return null;

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        Integer uid = (Integer)claims.get("uid");
        return uid.intValue();
    }

    //根据token字符串得到用户名称
    public static String getUsername(String token) {
        if(StringUtils.isEmpty(token)) return "";

        Jws<Claims> claimsJws = Jwts.parser().setSigningKey(tokenSignKey).parseClaimsJws(token);
        Claims claims = claimsJws.getBody();
        return (String)claims.get("username");
    }

}
