package architecture.community.util;

import org.apache.commons.lang3.RegExUtils;

public class SecuredCodeShield {
    
    public static String shieldCRLF(String text){
        return RegExUtils.replaceAll(text, "[\r\n]+","" );
    }

}
