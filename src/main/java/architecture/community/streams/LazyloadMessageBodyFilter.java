package architecture.community.streams;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class LazyloadMessageBodyFilter implements MessageBodyFilter{

    @Override
    public String process(String body) {

        String bodyToUse = body;
        if(StringUtils.isNotEmpty(bodyToUse) && StringUtils.countMatches(bodyToUse, "<img") > 0 )
		{
			Document doc = Jsoup.parse(bodyToUse);
			Elements eles = doc.select("img");
			for( Element ele : eles ) { 				
				String src = ele.attr("src"); 
				ele.attr("data-src", src);
			} 
			bodyToUse = doc.toString();
		}
        return bodyToUse;
    }
    
}
