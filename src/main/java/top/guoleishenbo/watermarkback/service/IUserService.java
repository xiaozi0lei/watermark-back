package top.guoleishenbo.watermarkback.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import top.guoleishenbo.watermarkback.entity.User;
import top.guoleishenbo.watermarkback.entity.dto.LoginDto;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
public interface IUserService extends IService<User> {
    JSONObject firstOrCreate(LoginDto loginDto);
}
