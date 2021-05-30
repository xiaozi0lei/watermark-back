package top.guoleishenbo.watermarkback.entity.dto;

import lombok.Data;

/**
 * 登录接口上行参数对象
 */
@Data
public class LoginDto {
    // wx code
    private String code;
    // wx 加密用户数据
    private String data;
    // wx 加密算法的初始向量
    private String iv;
    //原始数据字符串
    private String signature;
    //校验用户信息字符串
    private String rawData;
}
