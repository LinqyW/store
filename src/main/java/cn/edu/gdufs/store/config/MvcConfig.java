package cn.edu.gdufs.store.config;

import cn.edu.gdufs.store.interceptor.LoginInterceptor;
import cn.edu.gdufs.store.interceptor.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:注册处理器拦截器
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /** 拦截器配置 */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 创建拦截器对象
        HandlerInterceptor loginInterceptor = new LoginInterceptor();

        // 对以下路径不进行拦截
        List<String> patterns = new ArrayList<String>();
        patterns.add("/users/reg");
        patterns.add("/users/login");
        patterns.add("/districts/**");
        patterns.add("/products/**");

        // 通过注册工具添加拦截器(.order()中的数值越小，越优先执行)
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns(patterns).order(1);

        // token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);
    }
}
