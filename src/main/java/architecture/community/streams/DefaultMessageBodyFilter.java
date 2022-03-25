package architecture.community.streams;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;

import architecture.ee.service.Repository;

public class DefaultMessageBodyFilter implements MessageBodyFilter {

    public static final String FILTER_NEWVALUE_KEY = "components.message-body-filter.newValue";
    public static final String FILTER_KEYWORDS_KEY = "components.message-body-filter.keywords";

    @Autowired
	@Qualifier("repository")
	private Repository repository;
 
    private Logger log = LoggerFactory.getLogger(getClass().getName());

    private String[] keywords = new String[]{};

    private String newValue ; 

    private boolean enabled = false ;

    @PostConstruct
	public void initialize() throws Exception { 

        if( this.repository != null){
            String str = repository.getSetupApplicationProperties().getStringProperty(FILTER_KEYWORDS_KEY, null);
            if (str != null)
                this.keywords = StringUtils.commaDelimitedListToStringArray(str);
            this.newValue = repository.getSetupApplicationProperties().getStringProperty(FILTER_NEWVALUE_KEY, "");
            if( this.keywords.length > 0 )
                this.enabled = true; 
        } 
        log.info("Message Body Filter with {} {} {}", keywords, newValue, enabled );    
        
	}

    @Override
    public String process(String body) {
        String bodyToUse = body;
        if( enabled ){  
            for( String oldPattern : keywords )
                bodyToUse = StringUtils.replace(bodyToUse, oldPattern, newValue);
        }
        return bodyToUse;
    }
    
}
