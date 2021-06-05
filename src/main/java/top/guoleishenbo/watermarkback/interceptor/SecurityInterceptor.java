package top.guoleishenbo.watermarkback.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import top.guoleishenbo.watermarkback.annotation.TokenValidate;
import top.guoleishenbo.watermarkback.entity.User;
import top.guoleishenbo.watermarkback.service.IUserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityInterceptor implements HandlerInterceptor {

    private IUserService userService;

    public SecurityInterceptor(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 验证权限
        if (this.tokenValidate(handler, request)) {
            return true;
        }
        // token 无效
        response.sendError(HttpStatus.UNAUTHORIZED.value(), "token 无效，请登录");
        return false;
    }

    private boolean tokenValidate(Object handler, HttpServletRequest request) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            // 获取方法上的注解
            TokenValidate tokenValidate = handlerMethod.getMethod().getAnnotation(TokenValidate.class);

            // 如果标记了注解，则判断权限
            if (tokenValidate != null) {
                String token;
                String bearerToken = request.getHeader("Authorization");
                System.out.println("------bearer: " + bearerToken);
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    token = bearerToken.substring(7);
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("api_token", token);
                    User user = userService.getOne(wrapper);
                    return user != null;
                }
                return true;
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }
}
