package architecture.community.security.spring.authentication.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.RegExUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

import architecture.community.util.SecuredCodeShield;
import architecture.community.web.util.ParamUtils;
import architecture.ee.util.StringUtils;
import io.jsonwebtoken.ExpiredJwtException;

public class JWTFilter extends OncePerRequestFilter {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired(required = false)
	@Qualifier("userDetailsService")
	private UserDetailsService userDetailsService;

	public JWTFilter(JwtTokenProvider jwtTokenProvider) {
		Assert.notNull(jwtTokenProvider, "JwtTokenProvider cannot be null");
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	public void afterPropertiesSet() {
		Assert.notNull(jwtTokenProvider, "JwtTokenProvider cannot be null");
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		String header = request.getHeader(JwtTokenProvider.HEADER_STRING);
		
		if(StringUtils.isNullOrEmpty(header)) {
			header = ParamUtils.getParameter(request, JwtTokenProvider.PARAM_STRING, null);
		}
		 
		if ( StringUtils.isNullOrEmpty(header) ) {
			filterChain.doFilter(request, response);
			return;
		} 
		
		try {
			String jwt = this.resolveToken(request);
			if (!StringUtils.isNullOrEmpty(jwt)) {
				if(logger.isDebugEnabled()){
	 				logger.debug("jwt token : {}", SecuredCodeShield.shieldCRLF(jwt.toString()) );
				}
				if (this.jwtTokenProvider.validateToken(jwt)) {
					Authentication authentication;
					if (userDetailsService != null) {
						authentication = this.jwtTokenProvider.getAuthentication(jwt, userDetailsService, true);
					} else {
						authentication = this.jwtTokenProvider.getAuthentication(jwt);
					}
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}
		} catch (ExpiredJwtException eje) {
			if(logger.isInfoEnabled()){
				logger.info("Security exception for user {} - {}",  eje.getClaims().getSubject(), eje.getMessage());
			}
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			if(logger.isDebugEnabled()){
				logger.debug("Exception " + eje.getMessage(), eje);
			}
			return;
		}
		filterChain.doFilter(request, response);
		//this.resetAuthenticationAfterRequest();
	}


	private void resetAuthenticationAfterRequest() {
		if(logger.isDebugEnabled()){
			logger.debug("reset authentication as null.");
		}
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	/**
	 * @param request
	 * @return
	 */
	private String resolveToken(HttpServletRequest request) { 
		String bearerToken = request.getHeader(JwtTokenProvider.HEADER_STRING);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
			return bearerToken.substring(7, bearerToken.length());
		} 
		bearerToken = ParamUtils.getParameter(request, JwtTokenProvider.PARAM_STRING, null);
		if(logger.isDebugEnabled()){
			logger.debug("bearerToken:{}", SecuredCodeShield.shieldCRLF(bearerToken));
		}
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
			return bearerToken.substring(7, bearerToken.length());
		}
		return null;
	}
}