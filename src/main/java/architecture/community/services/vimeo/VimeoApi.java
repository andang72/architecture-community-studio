package architecture.community.services.vimeo;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignature;
import com.github.scribejava.core.oauth2.bearersignature.BearerSignatureURIQueryParameter;

/**
 * This API for VIMEO
 * 
 * @author donghyuck
 *
 */
public class VimeoApi extends DefaultApi20 {

	protected VimeoApi() {
	}

	private static class InstanceHolder {
		private static final VimeoApi INSTANCE = new VimeoApi();
	}

	public static VimeoApi instance() {
		return InstanceHolder.INSTANCE;
	}

	@Override
	public String getAccessTokenEndpoint() {
		return "https://vimeo.com/oauth/access_token";
	}

	@Override
	protected String getAuthorizationBaseUrl() {
		return "https://vimeo.com/oauth/authorize";
	}

	@Override
	public BearerSignature getBearerSignature() {
		return BearerSignatureURIQueryParameter.instance();
	}
}
