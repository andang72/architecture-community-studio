package tests;

import java.io.IOException;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import kr.dogfoot.hwplib.object.HWPFile;
import kr.dogfoot.hwplib.reader.HWPReader;

public class HwpTest {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void hwpReadTest() throws IOException, Exception {
        
        System.out.println("----");
        // String url = "http://ocwork.haansoft.com/sample/sample.hwp";
        // HWPFile hwpFile = HWPReader.fromURL(url);
        ResourceLoader loader = new DefaultResourceLoader();
        Resource resource = loader.getResource("sample.hwp");
        
        HWPFile hwpFile = HWPReader.fromInputStream(resource.getInputStream());
        System.out.println("----"+hwpFile.getBodyText().toString()) ;
        log.debug("--{}", resource);
    }

    public static void main(String[] args){
        System.out.println("----");
        HwpTest test = new HwpTest();
        try {
            test.hwpReadTest();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
