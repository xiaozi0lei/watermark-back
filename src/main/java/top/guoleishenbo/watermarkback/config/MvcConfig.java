package top.guoleishenbo.watermarkback.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.guoleishenbo.watermarkback.interceptor.SecurityInterceptor;
import top.guoleishenbo.watermarkback.service.IUserService;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    private IUserService userService;

    public MvcConfig(IUserService userService) {
        this.userService = userService;
    }

    @Bean
    SecurityInterceptor securityInterceptor(IUserService userService) {
        return new SecurityInterceptor(userService);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor(userService)).excludePathPatterns("/usr/login")
                .excludePathPatterns("/user/updateInfo").addPathPatterns("/**");
    }

}
