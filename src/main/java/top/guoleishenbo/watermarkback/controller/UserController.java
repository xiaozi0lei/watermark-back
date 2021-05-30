package top.guoleishenbo.watermarkback.controller;


import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import top.guoleishenbo.watermarkback.entity.dto.LoginDto;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @PostMapping("/login")
    public JSONObject login(@RequestBody LoginDto loginDto) {


        String token = RandomStringUtils.randomAlphanumeric(80);
        log.debug("token is : {}", token);
        JSONObject result = new JSONObject();
        result.put("token", token);
        return result;
    }
}

