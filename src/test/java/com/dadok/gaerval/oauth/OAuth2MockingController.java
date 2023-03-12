package com.dadok.gaerval.oauth;

import static com.dadok.gaerval.controller.ControllerSliceTest.*;
import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;

import java.io.IOException;
import java.util.UUID;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.util.CookieUtil;

import lombok.RequiredArgsConstructor;

/**
 * Unit Test용 컨트롤러
 */
@RestController
@RequiredArgsConstructor
public class OAuth2MockingController {

	@GetMapping("/oauth2/authorize/{provider}")
	public void oauth(
		@PathVariable("provider") AuthProvider provider,
		@RequestParam(required = false, value = "redirect_uri") String redirectUri,
		HttpServletResponse response
	) throws IOException {

		String redirectUriWithToken = UriComponentsBuilder.fromUriString(redirectUri)
			.queryParam("access_token", MOCK_ACCESS_TOKEN)
			.build().toUriString();

		CookieUtil.addCookie(response,
			REFRESH_TOKEN_COOKIE_NAME,
			UUID.randomUUID().toString(),
			1209600);

		response.sendRedirect(redirectUriWithToken);
	}

}
