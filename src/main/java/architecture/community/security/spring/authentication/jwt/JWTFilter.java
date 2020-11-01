package architecture.community.security.spring.authentication.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

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
	 			logger.debug("jwt token : {}", jwt);
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
			logger.info("Security exception for user {} - {}", eje.getClaims().getSubject(), eje.getMessage());
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			logger.debug("Exception " + eje.getMessage(), eje);
			return;
		}
		
		filterChain.doFilter(request, response);
		
		this.resetAuthenticationAfterRequest();
	}


	private void resetAuthenticationAfterRequest() {
		logger.debug("reset authentication as null.");
		SecurityContextHolder.getContext().setAuthentication(null);
	}

	private String resolveToken(HttpServletRequest request) {
		
		String bearerToken = request.getHeader(JwtTokenProvider.HEADER_STRING);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
			String jwt = bearerToken.substring(7, bearerToken.length());
			return jwt;
		}
		
		bearerToken = ParamUtils.getParameter(request, JwtTokenProvider.PARAM_STRING, null);
		logger.debug("bearerToken:{}", bearerToken);
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JwtTokenProvider.TOKEN_PREFIX)) {
			String jwt = bearerToken.substring(7, bearerToken.length());
			return jwt;
		}

		return null;
	}
}