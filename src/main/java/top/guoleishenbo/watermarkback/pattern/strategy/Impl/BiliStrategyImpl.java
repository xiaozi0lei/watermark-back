package top.guoleishenbo.watermarkback.pattern.strategy.Impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;
import top.guoleishenbo.watermarkback.pattern.strategy.VideoStrategy;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BiliStrategyImpl implements VideoStrategy {

    private RestTemplate restTemplate;
    private RestTemplate laxRestTemplate;

    public BiliStrategyImpl(RestTemplate restTemplate, RestTemplate laxRestTemplate) {
        this.restTemplate = restTemplate;
        this.laxRestTemplate = laxRestTemplate;
    }

    @Override
    public BaseResponse<VideoInfoVo> parse(String videoUrl) {
        BaseResponse<VideoInfoVo> result = new BaseResponse<>();
        VideoInfoVo videoInfo = new VideoInfoVo();
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
        headers.set("origin", "https://m.bilibili.com");
        headers.set("referer", "https://m.bilibili.com/");
        // 获取 aid
        String bvIdUrl = null;
        String aId = null;
        String cId = null;
        String imageUrl = null;
        String originString = null;
        HttpEntity<String> res = laxRestTemplate
                .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);
        System.out.println(res);

        URI location = res.getHeaders().getLocation();
        if (location != null) {
            bvIdUrl = location.toString();
            System.out.println("bvIdUrl is: " + bvIdUrl);
        }
        // 注意几个请求参数
        ResponseEntity avIdRes = restTemplate
                .exchange(bvIdUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);

        originString = avIdRes.toString();
        if (avIdRes.getStatusCode() == HttpStatus.FOUND) {
            ResponseEntity avIdResTwo = restTemplate
                    .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                            String.class);
            originString = avIdResTwo.toString();
        }
        System.out.println(originString);

        String patternString = "aid: ([0-9]+),";
        aId = regExGetResult(originString, patternString);
        if (aId == null) {

            ResponseEntity avIdResTwo = restTemplate
                    .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                            String.class);
            originString = avIdResTwo.toString();
        }
        aId = regExGetResult(originString, patternString);

        patternString = "cid: ([0-9]+),";
        cId = regExGetResult(originString, patternString);
        patternString = "readyPoster: '(.*?)'";
        imageUrl = regExGetResult(originString, patternString);
        System.out.println(imageUrl);

// https://api.bilibili.com/x/player/playurl?cid=323342619&avid=502525590&platform=html5&otype=json&qn=16&type=mp4&html5=1
        String playUrl = "https://api.bilibili.com/x/player/playurl?avid=" + aId + "&cid=" + cId
                + "&otype=json&qn=16&type=mp4";

        ResponseEntity<JSONObject> playRes = restTemplate
                .exchange(playUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        JSONObject.class);
        String downloadUrl = playRes.getBody().getJSONObject("data").getJSONArray("durl")
                .getJSONObject(0).getString("url");
        System.out.println(downloadUrl);

        // downloadUrl 可能是302跳转地址，检验
//        headers.set("Referer", "https://www.bilibili.com");
//        ResponseEntity checkRes = restTemplate
//                .exchange(downloadUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
//                        String.class);
//        System.out.println("sunguolei: " + checkRes.getStatusCode());
//        if (checkRes.getStatusCode() == HttpStatus.FOUND) {
//            System.out.println("hello");
//        }
        videoInfo.setImageUrl("http:" + imageUrl);
        videoInfo.setDownloadUrl(downloadUrl);
        videoInfo.setPlayUrl(downloadUrl);
        videoInfo.setSaveUrl(downloadUrl);

        result.setData(videoInfo);
        return result;
    }

    public static void main(String[] args) {
        String videoUrl = "https://b23.tv/1sLtYh";
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent",
                "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
        headers.set("origin", "https://m.bilibili.com");
        headers.set("referer", "https://m.bilibili.com/");
        // 获取 bvId
        String bvIdUrl = null;
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(2000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(72000);
        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        httpRequestFactory.setHttpClient(httpClient);
        RestTemplate laxRestTemplate = new RestTemplate(httpRequestFactory);

        // 注意几个请求参数
        HttpEntity<String> res = laxRestTemplate
                .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);
        System.out.println(res);

        URI location = res.getHeaders().getLocation();
        if (location != null) {
            bvIdUrl = location.toString();
            System.out.println("bvIdUrl is: " + bvIdUrl);
        }

//        String patternString = "video/(.*?)\\?";
//        String bvId = regExGetResult(bvIdUrl, patternString);

        // 获取 aid
        String aId = null;
        String cId = null;
        String originString = null;
        RestTemplate restTemplate = new RestTemplate();
        // 注意几个请求参数
        ResponseEntity avIdRes = restTemplate
                .exchange(bvIdUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);

        originString = avIdRes.toString();
        if (avIdRes.getStatusCode() == HttpStatus.FOUND) {
            ResponseEntity avIdResTwo = restTemplate
                    .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                            String.class);
            originString = avIdResTwo.toString();
        }
        System.out.println(originString);

        String patternString = "aid: ([0-9]+),";
        aId = regExGetResult(originString, patternString);
        patternString = "cid: ([0-9]+),";
        cId = regExGetResult(originString, patternString);
        patternString = "readyPoster: '(.*?)'";
        String imageUrl = regExGetResult(originString, patternString);
        System.out.println("aId is " + aId);
        System.out.println("cId is " + cId);
        System.out.println(imageUrl);

        String playUrl = "https://api.bilibili.com/x/player/playurl?avid=" + aId + "&cid=" + cId + "&otype=json";

        ResponseEntity<JSONObject> playRes = restTemplate
                .exchange(playUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        JSONObject.class);
        String downloadUrl = playRes.getBody().getJSONObject("data").getJSONArray("durl")
                .getJSONObject(0).getString("url");
        String avReferer = "https://www.bilibili.com/video/av" + aId;
        System.out.println(downloadUrl);
//
//        headers.set("Referer", avReferer);
//
//        ResponseEntity downloadRes = restTemplate
//                .exchange(downloadUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
//                        String.class);
//
//        System.out.println(downloadRes.getStatusCode());
    }

    public static String regExGetResult(String sourceString, String regEx) {
        String result = null;

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regEx);

        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(sourceString);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
            result = m.group(1);
            System.out.println("get regEx result is :" + result);
            return result;
        } else {
            System.out.println("NO MATCH");
        }
        return null;
    }
}
