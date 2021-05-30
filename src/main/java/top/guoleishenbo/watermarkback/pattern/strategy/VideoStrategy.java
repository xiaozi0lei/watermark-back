package top.guoleishenbo.watermarkback.pattern.strategy;

import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;

public interface VideoStrategy {
    public VideoInfoVo parse(String videoUrl);
}
