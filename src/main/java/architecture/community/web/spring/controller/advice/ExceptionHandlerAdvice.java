/**
 *    Copyright 2015-2017 donghyuck
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package architecture.community.web.spring.controller.advice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import architecture.community.web.model.Result;


@ControllerAdvice(basePackages={ "architecture.community.web.spring.controller.data", "architecture.community.web.spring.controller.gateway" })
public class ExceptionHandlerAdvice {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
	@ExceptionHandler
	@ResponseBody
	public Result handleExceptioin(HttpServletRequest request, HttpServletResponse response, Exception e){ 
		logger.info("Exception Occured:: URL="+request.getRequestURL());
		logger.debug("Details ---" , e);
		if( e instanceof org.springframework.security.authentication.BadCredentialsException ){
			response.setStatus(HttpStatus.UNAUTHORIZED.value());
		}
		else if( e instanceof org.springframework.security.access.AccessDeniedException ){
			response.setStatus(HttpStatus.FORBIDDEN.value());
		}else{
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		} 
		Result r = Result.newResult(e);			
		return r;
	} 
}
