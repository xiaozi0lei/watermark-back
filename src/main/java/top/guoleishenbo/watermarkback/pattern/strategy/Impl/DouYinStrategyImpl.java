package top.guoleishenbo.watermarkback.pattern.strategy.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import top.guoleishenbo.watermarkback.entity.base.BaseResponse;
import top.guoleishenbo.watermarkback.entity.vo.VideoInfoVo;
import top.guoleishenbo.watermarkback.pattern.strategy.Video;
import top.guoleishenbo.watermarkback.pattern.strategy.VideoStrategy;

import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DouYinStrategyImpl implements VideoStrategy {

    private RestTemplate restTemplate;
    private RestTemplate laxRestTemplate;

    public DouYinStrategyImpl(RestTemplate restTemplate, RestTemplate laxRestTemplate) {
        this.restTemplate = restTemplate;
        this.laxRestTemplate = laxRestTemplate;
    }

    @Override
    public BaseResponse<VideoInfoVo> parse(String videoUrl) {
        BaseResponse<VideoInfoVo> result = new BaseResponse<>();
        String url = null;
        String itemId = null;
        VideoInfoVo videoInfo = new VideoInfoVo();
        // 先从url上截取 item_id
        if (videoUrl.contains("/share/video")) {
            url = videoUrl;
        } else {
            //使用
//            OkHttpClient client = new OkHttpClient.Builder()
//                    .followRedirects(false);  //禁制OkHttp的重定向操作，我们自己处理重定向
//                    .addInterceptor(new RedirectInterceptor())
//                    .build();
            HttpHeaders headers = new HttpHeaders();
            headers.set("user-agent",
                    "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

            // 注意几个请求参数
            HttpEntity<String> res = laxRestTemplate
                    .exchange(videoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                            String.class);
            URI location = res.getHeaders().getLocation();
            if (location != null) {
                url = location.toString();
            }
            // 处理 url 拿到 item_id
            String patternString = "/video/([0-9]+)/";

            // 创建 Pattern 对象
            Pattern pattern = Pattern.compile(patternString);

            // 现在创建 matcher 对象
            Matcher m = pattern.matcher(url);
            if (m.find()) {
                System.out.println("Found value: " + m.group(0));
                itemId = m.group(1);
            } else {
                System.out.println("NO MATCH");
            }
            System.out.println(res);
        }
        // 通过 item_id 调用接口获取水印视频接口返回信息，获取 play_addr uri
        String itemInfoUrl = "https://www.iesdouyin.com/web/api/v2/aweme/iteminfo?item_ids=" + itemId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");
        headers.set("Referer", "https://www.iesdouyin.com");
        headers.set("Host", "www.iesdouyin.com");

        // 注意几个请求参数
        HttpEntity<String> res = restTemplate
                .exchange(itemInfoUrl, HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);
        JSONObject resJson = JSON.parseObject(res.getBody());
        String videoId = null;
        try {
            videoId = resJson.getJSONArray("item_list").getJSONObject(0).getJSONObject("video")
                    .getJSONObject("play_addr").getString("uri");
            String imageUrl = null;
            imageUrl = resJson.getJSONArray("item_list").getJSONObject(0).getJSONObject("video")
                    .getJSONObject("cover").getJSONArray("url_list").getString(0);
            videoInfo.setImageUrl(imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 通过 uri 调用接口获取视频
        String playUrl = "https://aweme.snssdk.com/aweme/v1/play/?video_id=" + videoId + "&ratio=720&line=0";
        videoInfo.setNoWaterUrl(playUrl);
        System.out.println("video url : " + videoUrl);

        HttpHeaders headersPlay = new HttpHeaders();
        headersPlay.set("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");
        HttpEntity<String> resultPlay = laxRestTemplate
                .exchange(playUrl, HttpMethod.GET, new HttpEntity<>(null, headersPlay),
                        String.class);

        URI location = resultPlay.getHeaders().getLocation();
        if (location != null) {
            System.out.println("play url : " + location.toString());

            videoInfo.setNoWaterUrl(location.toString());
        }

        result.setData(videoInfo);
        return result;
    }

    public static void main(String[] args) {
        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setConnectionRequestTimeout(2000);
        httpRequestFactory.setConnectTimeout(10000);
        httpRequestFactory.setReadTimeout(72000);
        HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling().build();
        httpRequestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(httpRequestFactory);
        HttpHeaders headers = new HttpHeaders();
        headers.set("user-agent",
                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.97 Safari/537.36");

        // 注意几个请求参数
        HttpEntity<String> res = restTemplate
                .exchange("https://v.douyin.com/emJhqeh/", HttpMethod.GET, new HttpEntity<>(null, headers),
                        String.class);
        System.out.println(res.getHeaders().getLocation());

        String url = res.getHeaders().getLocation().toString();
        String patternString = "/video/([0-9]+)/";

        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(patternString);

        // 现在创建 matcher 对象
        Matcher m = pattern.matcher(url);
        if (m.find()) {
            System.out.println("Found value: " + m.group(0));
        } else {
            System.out.println("NO MATCH");
        }
    }
}
