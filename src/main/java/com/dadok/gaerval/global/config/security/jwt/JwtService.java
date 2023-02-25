package com.dadok.gaerval.global.config.security.jwt;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.UserRepository;
import com.dadok.gaerval.global.config.security.CustomOAuth2UserService;
import com.dadok.gaerval.global.config.security.UserPrincipal;
import com.dadok.gaerval.global.error.exception.UserNotFoundException;
import com.dadok.gaerval.infra.redis.RemoteCacheService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtService {

	public static final String ACCESS_TOKEN_HEADER_NAME = "Authorization";

	public static final String REFRESH_TOKEN_HEADER_NAME = "RefreshToken";

	public static final String AUTHENTICATION_TYPE_BEARER = "Bearer";

	private final JwtProvider jwtProvider;

	private final UserRepository userRepository;

	private final RemoteCacheService cacheService;

	private final CustomOAuth2UserService customOAuth2UserService;

	public String createAccessToken(Long userId, Collection<GrantedAuthority> authorities) {
		return jwtProvider.createAccessToken(userId, authorities);
	}

	public void validate(String accessToken) {
		jwtProvider.validate(accessToken);
	}

	@Transactional(readOnly = true)
	public Authentication getAuthentication(String accessToken) {
		Long id = this.jwtProvider.getId(accessToken);
		User user = userRepository.findByIdWithAuthorities(id)
			.orElseThrow(UserNotFoundException::new);

		UserPrincipal userPrincipal = UserPrincipal.of(user);

		return new JwtAuthenticationToken(userPrincipal, accessToken);
	}

}
