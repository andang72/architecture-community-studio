package tests;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.util.TextUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import architecture.community.services.HttpClientService;
import architecture.community.services.support.PooledHttpClientSupport.ResponseCallBack;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration("WebContent/")
@ContextConfiguration(locations = { "classpath:test-utils-context.xml" })
public class ImageFromUrlTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    @Qualifier("communityHttpClientService")
    private HttpClientService communityHttpClientService;


    public void srcsetTest() {
        String src = "https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008-683x1024.jpg 683w, https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008-480x720.jpg 480w, https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008-768x1152.jpg 768w, https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008-1024x1536.jpg 1024w, https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008-408x612.jpg 408w, https://nudegirlsalert.com/wp-content/uploads/2022/10/SexArt_Innocent-Fun_Mila-Azul_high_0008.jpg 1067w";

        String[] srcset = StringUtils.commaDelimitedListToStringArray(src);
        String ss = null;
        for (String s : srcset) {
            System.out.println("------>" + s);
            String[] a = StringUtils.split(s.trim(), " ");
            ss = a[0];
            System.out.println("------>>" + a[0]);
        }
        System.out.println("------");
        System.out.println(ss);
    }

    String getMaxImageFromSrcset(String srcset) {
        String[] srcarray = StringUtils.commaDelimitedListToStringArray(srcset);
        java.util.Map<Integer, String> map = new java.util.HashMap<Integer, String>();
        for (String src : srcarray) {
            String[] trimed = StringUtils.split(src.trim(), " ");
            map.put(NumberUtils.createInteger(StringUtils.replace(trimed[1], "w", "")), trimed[0]);
        }
        System.out.println( map );
        return map.get(Collections.max(map.keySet()));
    }

    @Test
    public void downloadTestFromNudegirlsalert() throws Exception {

        String serviceUrl = "https://epicpornpics.com/ariana-reychers-hairy-and-naturally-busty/";
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("User-Agent", HttpClientService.USER_AGENT);
        Document doc = communityHttpClientService.get(serviceUrl, headers, null, new ResponseCallBack<Document>() {
            public Document process(int statusCode, String responseString) {
                return Jsoup.parse(responseString);
            }
        });

        headers.put("Referer", serviceUrl);
        Elements eles = doc.select("article .gallery-icon a > img");
        // System.out.println(eles.toString());
        for (Element ele : eles) {
            String source = ele.attr("src").trim();
            String srcset = ele.attr("srcset").trim();

            if( ele.parent().hasClass("custom-link") )
            {
                System.out.println("skip: " + source);
                continue;
            }

            if (StringUtils.hasText(srcset)) {
                String maxSrc = getMaxImageFromSrcset(srcset);
                if (StringUtils.hasText(maxSrc))
                    source = maxSrc;
            }else{
                String maxSrc = ele.parent().attr("href");
                if (StringUtils.hasText(maxSrc))
                    source = maxSrc;
            }
            
            String title = ele.attr("alt").trim();
            // source = StringUtils.remove(source, "thumbs/th_");
            System.out.println("title: " + title);
            System.out.println("source: " + source);
        }
    }

}
