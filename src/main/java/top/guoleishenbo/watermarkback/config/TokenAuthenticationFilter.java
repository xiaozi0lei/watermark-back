package top.guoleishenbo.watermarkback.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import top.guoleishenbo.watermarkback.entity.User;
import top.guoleishenbo.watermarkback.service.IUserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@WebFilter(urlPatterns = "/*")
@Order(value = 1)
@Component
public class TokenAuthenticationFilter extends GenericFilterBean {
    private static final Set<String> ALLOWED_PATHS = Collections.unmodifiableSet(new HashSet<>(
            Arrays.asList("/user/updateInfo")));

    private IUserService userService;

    public TokenAuthenticationFilter(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String path = request.getRequestURI().substring(request.getContextPath().length()).replaceAll("[/]+$", "");
        boolean allowedPath = ALLOWED_PATHS.contains(path);

        if (allowedPath) {
            System.out.println("这里是不需要处理的url进入的方法");
            filterChain.doFilter(request, response);
        } else {
            System.out.println("这里是需要处理的url进入的方法");
            try {
                String token = null;
                String bearerToken = request.getHeader("Authorization");
                System.out.println("------bearer: " + bearerToken);
                if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                    token = bearerToken.substring(7);
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    wrapper.eq("api_token", token);
                    User user = userService.getOne(wrapper);
                    if (user == null) {
                        response.setStatus(HttpStatus.UNAUTHORIZED.value());
                        response.getWriter().write("Invalid token");
                        response.getWriter().flush();
                        return;
                    }
                } else {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Invalid token");
                    response.getWriter().flush();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid token");
                response.getWriter().flush();
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
