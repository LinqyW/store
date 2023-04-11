package cn.edu.gdufs.store.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:定义处理器拦截器
 */
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前用户是否已登录
        if (request.getSession().getAttribute("uid") == null) {
            // 没有，需要拦截，设置状态码
            response.setStatus(401);
//            response.sendRedirect("http://localhost:8080/users/login");
            return false;
        }
        return true;
    }
}
