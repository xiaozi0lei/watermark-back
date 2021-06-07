package top.guoleishenbo.watermarkback.pattern.strategy.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;
import top.guoleishenbo.watermarkback.pattern.strategy.VideoStrategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class WeiShiStrategyImpl implements VideoStrategy {

    private RestTemplate restTemplate;
    private RestTemplate laxRestTemplate;

    public WeiShiStrategyImpl(RestTemplate restTemplate, RestTemplate laxRestTemplate) {
        this.restTemplate = restTemplate;
        this.laxRestTemplate = laxRestTemplate;
    }

    @Override
    public BaseResponse<VideoInfoVo> parse(String videoUrl) {
        BaseResponse<VideoInfoVo> result = new BaseResponse<>();
        String feedId = null;
        VideoInfoVo videoInfo = new VideoInfoVo();
        // 处理 url 拿到 feedId
        String patternString = "id=(.*?)&";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(patternString);

        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(videoUrl);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            feedId = m.group(1);
        } else {
            System.out.println("NO MATCH");
        }

        // 通过 feedId 调用接口获取水印视频接口返回信息，获取 play_addr uri
        String itemInfoUrl = "https://h5.qzone.qq.com/webapp/json/weishi/WSH5GetPlayPage?t=0.4185745904612037&g_tk=";
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

        JSONObject feedIdObject = new JSONObject();
        feedIdObject.put("feedid", feedId);
        // 注意几个请求参数
        HttpEntity<String> res = restTemplate
                .exchange(itemInfoUrl, HttpMethod.POST, new HttpEntity<>(feedIdObject, headers),
                        String.class);
        JSONObject resJson = JSON.parseObject(res.getBody());
        try {
            String imageUrl = resJson.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).getJSONArray("images")
                    .getJSONObject(0).getString("url");
            videoInfo.setImageUrl(imageUrl);
            String downloadUrl = resJson.getJSONObject("data").getJSONArray("feeds").getJSONObject(0).getString("video_url");
            videoInfo.setDownloadUrl(downloadUrl);
            videoInfo.setPlayUrl(downloadUrl);
            videoInfo.setSaveUrl(downloadUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        result.setData(videoInfo);
        return result;
    }

}
