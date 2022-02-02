package architecture.studio.web.functions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.NativeWebRequest;

import architecture.community.page.Parameter;
import architecture.community.page.api.Api;
import architecture.community.web.model.Result;

public class Utils { 

    public static String getParameter( NativeWebRequest request, Api api, String paramName ){
        String value = request.getParameter(paramName);
        if( StringUtils.isBlank(value)){
            for(Parameter param : api.getParameters() ){
                if( StringUtils.equals(param.getKey(), paramName) )
                    value = param.getValue();
            }
        } 
       
        return value;
    }

    public static Result newIllegalArgumentException(String msg){
        return Result.newResult(new IllegalArgumentException(msg));
    }
}
