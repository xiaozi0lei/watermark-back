package top.guoleishenbo.watermarkback.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;
import top.guoleishenbo.watermarkback.pattern.strategy.Impl.DouYinStrategyImpl;
import top.guoleishenbo.watermarkback.pattern.strategy.Video;
import top.guoleishenbo.watermarkback.pattern.strategy.VideoStrategy;
import top.guoleishenbo.watermarkback.utility.http.UrlUtility;

@RequestMapping("/video")
@RestController
public class VideoController {
    private RestTemplate restTemplate;
    private RestTemplate laxRestTemplate;

    public VideoController(RestTemplate restTemplate, RestTemplate laxRestTemplate) {
        this.restTemplate = restTemplate;
        this.laxRestTemplate = laxRestTemplate;
    }

    @PostMapping("/parse")
    public BaseResponse parse(@RequestBody String videoUrl) {
        String url = UrlUtility.findUrlByStr(videoUrl);
        VideoStrategy strategy = null;

        if (url.contains("douyin.com") || url.contains("iesdouyin.com")) {
            strategy = new DouYinStrategyImpl(restTemplate, laxRestTemplate);
        } else {
            throw new RuntimeException("不支持的视频格式");
        }

        Video video = new Video(strategy);
        return video.parse(url);
    }

    public static void main(String[] args) {
        String testUrl = "1.5 pd:/ 旧衣服回收挣钱不？怎么收，怎么卖，看完你就明白了  https://v.douyin.com/emJhqeh/ 复制Ci链接，打开Dou愔搜索，直接观看视頻！";
        String url = UrlUtility.findUrlByStr(testUrl);
        System.out.println(url);
    }
}
