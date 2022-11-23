package architecture.community.web.gateway;

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

    public static Long getLongParameter( NativeWebRequest request, Api api, String paramName ){
        String value = request.getParameter(paramName);
        if( StringUtils.isBlank(value)){
            for(Parameter param : api.getParameters() ){
                if( StringUtils.equals(param.getKey(), paramName) )
                    value = param.getValue();
            }
        } 
        try{ 
        return Long.parseLong(value); 
        }catch(Throwable e ){
            return 0L;
        }
    }

    public static Integer getIntParameter( NativeWebRequest request, Api api, String paramName ){
        String value = request.getParameter(paramName);
        if( StringUtils.isBlank(value)){
            for(Parameter param : api.getParameters() ){
                if( StringUtils.equals(param.getKey(), paramName) )
                    value = param.getValue();
            }
        } 
        try{ 
        return Integer.parseInt(value); 
        }catch(Throwable e ){
            return 0;
        }
    }

    public static Result newIllegalArgumentException(String msg){
        return Result.newResult(new IllegalArgumentException(msg));
    }
}
