package top.guoleishenbo.watermarkback.utility.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtility {
    public static String findUrlByStr(String data) {
//		data = "地球亚洲中国https://www.baidu.com/s?wd=java 厉害厉害！";
        Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
        Matcher matcher = pattern.matcher(data);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

}
