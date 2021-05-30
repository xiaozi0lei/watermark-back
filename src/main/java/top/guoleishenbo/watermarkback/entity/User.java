package top.guoleishenbo.watermarkback.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author sunni
 * @since 2021-05-30
 */
@Data
public class User {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 验证邮箱时间
     */
    private LocalDateTime emailVerifiedAt;

    /**
     * token
     */
    private String apiToken;

    /**
     * wx openid
     */
    private String openid;

    /**
     * wx unionid
     */
    private String unionid;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 性别（1男|0女）
     */
    private Integer gender;

    /**
     * 国别
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;


}
