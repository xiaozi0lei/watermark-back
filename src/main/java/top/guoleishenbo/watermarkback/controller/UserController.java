package top.guoleishenbo.watermarkback.controller;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.guoleishenbo.watermarkback.entity.dto.LoginDto;
import top.guoleishenbo.watermarkback.service.IUserService;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    private IUserService userService;

    public UserController(IUserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public JSONObject login(@RequestBody LoginDto loginDto) {

        // 根据 openid 查询用户是否存在，存在更新 token，不存在创建用户，关联 openid，返回新 token
        return userService.firstOrCreate(loginDto);
    }
}

