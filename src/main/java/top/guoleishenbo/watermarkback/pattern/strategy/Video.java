package top.guoleishenbo.watermarkback.pattern.strategy;

import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;

public class Video {
    private VideoStrategy strategy;

    public Video(VideoStrategy strategy) {
        this.strategy = strategy;
    }

    public BaseResponse<VideoInfoVo> parse(String videoUrl) {
        return this.strategy.parse(videoUrl);
    }
}
