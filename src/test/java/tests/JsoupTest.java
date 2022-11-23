package tests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import architecture.ee.util.StringUtils; 

public class JsoupTest {
    
    @Test
    public void testJsopParse(){
     
        String text = "<figure class='media'><div data-oembed-url='https://youtu.be/3dWy50rFgvg'><div style='position: relative; padding-bottom: 100%; height: 0; padding-bottom: 56.2493%;'><iframe src='https://www.youtube.com/embed/3dWy50rFgvg' style='position: absolute; width: 100%; height: 100%; top: 0; left: 0;' frameborder='0' allow='autoplay; encrypted-media' allowfullscreen=''></iframe></div></div></figure>";
        Document doc = Jsoup.parse(text);
        Elements eles = doc.select("figure.media div[data-oembed-url]");
        for( Element ele : eles ) { 
            String src =  ele.attr("data-oembed-url");
            if( StringUtils.startsWithIgnoreCase(src, "https://youtu.be/"))
            {
               
                String id = StringUtils.delete(src, "https://youtu.be/");
                StringBuilder sb = new StringBuilder("https://img.youtube.com/vi/").append(id).append("/0.jpg");
                System.out.println( sb.toString() );
            }
        }
    }
}
