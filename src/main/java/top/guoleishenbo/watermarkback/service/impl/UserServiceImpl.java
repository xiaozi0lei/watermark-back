package top.guoleishenbo.watermarkback.service.impl;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.config.impl.WxMaDefaultConfigImpl;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import top.guoleishenbo.watermarkback.entity.User;
import top.guoleishenbo.watermarkback.entity.dto.LoginDto;
import top.guoleishenbo.watermarkback.mapper.UserMapper;
import top.guoleishenbo.watermarkback.service.IUserService;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Value("${wx.appId}")
    private String appId;
    @Value("${wx.appSecret}")
    private String appSecret;

    private UserMapper userMapper;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public JSONObject firstOrCreate(LoginDto loginDto) {
        final WxMaService wxService = getWxMaService();

        String unionId = null;
        String openId = null;
        String sessionKey = null;

        // 获取微信用户session
        WxMaJscode2SessionResult session = null;
        try {
            session = wxService.getUserService().getSessionInfo(loginDto.getCode());

            openId = session.getOpenid();
            unionId = session.getUnionid();
            sessionKey = session.getSessionKey();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RuntimeException("login handler error, getSessionInfo 抛出 WxErrorException");
        }
//        if (null == session) {
//            throw new RuntimeException("login handler error");
//        }

//        // 解密用户信息
//        WxMaUserInfo wxUserInfo = wxService.getUserService().getUserInfo(session.getSessionKey(),
//                loginDto.getEncryptedData(), loginDto.getIv());
//        if (null == wxUserInfo) {
//            throw new RuntimeException("wxUser not exist");
//        }
//
//        // 解密手机号码信息
//        WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxService.getUserService().getPhoneNoInfo(session.getSessionKey(),
//                loginDto.getEncryptedData(), loginDto.getIv());
//        if (Objects.isNull(wxMaPhoneNumberInfo) || StringUtils.isBlank(wxMaPhoneNumberInfo.getPhoneNumber())) {
//            // 解密手机号码信息错误
//        }
//
//        System.out.printf("============用户登录注册获取微信用户信息===========> openId=%s, username=%s", wxUserInfo.getOpenId(), wxUserInfo.getNickName());
//        String unionId = wxUserInfo.getUnionId();
//        String openId = wxUserInfo.getOpenId();
//        String nickName = wxUserInfo.getNickName();
//        String avatarUrl = wxUserInfo.getAvatarUrl();
//        String gender = wxUserInfo.getGender();
//        String country = wxUserInfo.getCountry();
//        String province = wxUserInfo.getProvince();
//        String city = wxUserInfo.getCity();

        String token = RandomStringUtils.randomAlphanumeric(80);
        log.debug("token is : {}", token);
        JSONObject result = new JSONObject();
        result.put("token", token);

        User newUser = new User();
        newUser.setApiToken(token);
        newUser.setOpenid(openId);
        newUser.setUnionid(unionId);
//        newUser.setName(nickName);
//        newUser.setAvatar(avatarUrl);
//        newUser.setGender(Integer.valueOf(gender));
//        newUser.setCountry(country);
//        newUser.setProvince(province);
//        newUser.setCity(city);

        // 先判断表中是否存在当前用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openId);
        // 根据 openid 判断用户是否存在数据库中，决定插入数据还是更新数据
        this.saveOrUpdate(newUser, wrapper);

        return result;
    }

    @Override
    public JSONObject updateInfo(LoginDto loginDto) {
        final WxMaService wxService = getWxMaService();

        // 获取微信用户session
        WxMaJscode2SessionResult session = null;
        String unionId = null;
        String openId = null;
        try {
            session = wxService.getUserService().getSessionInfo(loginDto.getCode());
            openId = session.getOpenid();
            unionId = session.getUnionid();
        } catch (WxErrorException e) {
            e.printStackTrace();
            throw new RuntimeException("login handler error, getSessionInfo 抛出 WxErrorException");
        }

        // 解密用户信息
        WxMaUserInfo wxUserInfo = wxService.getUserService().getUserInfo(session.getSessionKey(),
                loginDto.getEncryptedData(), loginDto.getIv());
        if (null == wxUserInfo) {
            throw new RuntimeException("wxUser not exist");
        }

        // 解密手机号码信息
        WxMaPhoneNumberInfo wxMaPhoneNumberInfo = wxService.getUserService().getPhoneNoInfo(session.getSessionKey(),
                loginDto.getEncryptedData(), loginDto.getIv());
        if (Objects.isNull(wxMaPhoneNumberInfo) || StringUtils.isBlank(wxMaPhoneNumberInfo.getPhoneNumber())) {
            // 解密手机号码信息错误
        }

        System.out.printf("============用户登录注册获取微信用户信息===========> openId=%s, username=%s", wxUserInfo.getOpenId(), wxUserInfo.getNickName());

        String nickName = wxUserInfo.getNickName();
        String avatarUrl = wxUserInfo.getAvatarUrl();
        String gender = wxUserInfo.getGender();
        String country = wxUserInfo.getCountry();
        String province = wxUserInfo.getProvince();
        String city = wxUserInfo.getCity();

        String token = RandomStringUtils.randomAlphanumeric(80);
        log.debug("token is : {}", token);
        JSONObject result = new JSONObject();
        result.put("token", token);

        User newUser = new User();
        newUser.setApiToken(token);
        newUser.setOpenid(openId);
        newUser.setUnionid(unionId);
        newUser.setName(nickName);
        newUser.setAvatar(avatarUrl);
        newUser.setGender(Integer.valueOf(gender));
        newUser.setCountry(country);
        newUser.setProvince(province);
        newUser.setCity(city);

        // 先判断表中是否存在当前用户
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("openid", openId);
        // 根据 openid 判断用户是否存在数据库中，决定插入数据还是更新数据
        this.saveOrUpdate(newUser, wrapper);

        return result;
    }

    private WxMaService getWxMaService() {
        WxMaDefaultConfigImpl config = new WxMaDefaultConfigImpl();
        config.setAppid(appId);
        config.setSecret(appSecret);
        config.setMsgDataFormat("JSON");
        WxMaService wxMaService = new WxMaServiceImpl();
        wxMaService.setWxMaConfig(config);
        return wxMaService;
    }

}
