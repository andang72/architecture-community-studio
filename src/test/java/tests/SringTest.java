package tests;

import org.apache.commons.lang3.RegExUtils;
import org.apache.commons.lang3.StringUtils;
import org.testng.annotations.Test;

public class SringTest {
    
    @Test
    public void testStringFormat(){
        
        String str = "39b280e88576d7.jpeg";

        System.out.println( str.lastIndexOf("."));
        System.out.println( str.substring(0, str.lastIndexOf(".") ));
        System.out.println( str.substring( str.lastIndexOf(".") ));

        String text = "123**&!!asdf#\n";
        System.out.println(text.replaceAll("[\r\n]","") );
        System.out.println( RegExUtils.replaceAll(text, "[\r\n]+","" ));
    }

    @Test
    public void testStringLength(){
       String str ="8a853c2-c8e1-4048-acfb-9c983ba80fde/viewimage.php?id=28b3c423f7c231a5&no=24b0d769e1d32ca73feb85fa11d02831b3f3a5b77d2b2273808df9809dbc3b5c22a5745f4521d9fb90599e5728102f3b57f436443f6fc685e5a6ffa89ad2045615dc587b66c50df122bbf731c9f95abfe5fd8b31d707e5d36acf9fe7e5d24b23f0cb6b70709095e323411bb059395b512ec5bdff93450e3b";
       if( str.length() > 256 )
       {
           System.out.println("too long..");
       }


       
    }
}
