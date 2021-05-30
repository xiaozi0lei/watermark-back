package top.guoleishenbo.watermarkback.service.impl;

import top.guoleishenbo.watermarkback.entity.User;
import top.guoleishenbo.watermarkback.mapper.UserMapper;
import top.guoleishenbo.watermarkback.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
