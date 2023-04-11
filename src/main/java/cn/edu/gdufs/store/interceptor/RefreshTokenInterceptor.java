package cn.edu.gdufs.store.interceptor;

import cn.edu.gdufs.store.util.JwtUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

import static cn.edu.gdufs.store.util.RedisConstants.LOGIN_USER_TTL;

/**
 * Description:token刷新的拦截器
 */
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1.获取请求头中的token
        String token = request.getHeader("token");
        //未登录，放行
        if (StrUtil.isBlank(token)) {
            return true;
        }
        // 2.基于TOKEN获取redis中的用户
        Integer uid = JwtUtil.getUid(token);
        String tokenRedis = stringRedisTemplate.opsForValue().get("user:" + uid);
        String key = "user:" + uid;
        // 3.判断用户是否存在
        if (tokenRedis.isEmpty()) {
            return true;
        }
        // 4.刷新token有效期
        stringRedisTemplate.expire(key, LOGIN_USER_TTL, TimeUnit.MINUTES);
        // 5.放行
        return true;
    }

}
