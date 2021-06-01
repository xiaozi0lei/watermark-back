package top.guoleishenbo.watermarkback.entity.base;

import lombok.Data;

@Data
public class BaseResponse<T> {
    private String code;
    private String message;
    private T data;

    public BaseResponse() {
        this.code = "200";
        this.message = "success";
    }
}
