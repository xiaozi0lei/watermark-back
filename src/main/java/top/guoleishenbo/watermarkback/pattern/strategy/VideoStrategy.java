package top.guoleishenbo.watermarkback.pattern.strategy;

import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;

public interface VideoStrategy {
    BaseResponse<VideoInfoVo> parse(String videoUrl);
}
