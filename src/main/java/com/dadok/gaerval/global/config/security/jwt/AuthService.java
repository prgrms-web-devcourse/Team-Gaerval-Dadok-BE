package com.dadok.gaerval.global.config.security.jwt;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dadok.gaerval.domain.auth.entity.RefreshToken;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFound;
import com.dadok.gaerval.domain.auth.repository.RefreshTokenRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.repository.UserRepository;
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

	private final RefreshTokenRepository refreshTokenRepository;

	@Value("${refresh-token.expiration-second}")
	private int refreshTokenExpirationSeconds;

	public String createAccessToken(Long userId, Collection<GrantedAuthority> authorities) {
		return jwtProvider.createAccessToken(userId, authorities);
	}

	@Transactional
	public RefreshToken createRefreshToken(Long userId, Collection<GrantedAuthority> authorities) {
		RefreshToken refreshToken = new RefreshToken(UUID.randomUUID().toString(), userId,
			refreshTokenExpirationSeconds,
			authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));

		return refreshTokenRepository.save(refreshToken);
	}

	public String reIssueAccessToken(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			throw new RefreshTokenAuthenticationNotFound();
		}

		RefreshToken findRefreshToken = refreshTokenRepository.findById(refreshToken)
			.orElseThrow(RefreshTokenAuthenticationNotFound::new);

		return jwtProvider.createAccessToken(findRefreshToken.getUserId(),
			findRefreshToken.getRoles().stream().map(SimpleGrantedAuthority::new)
				.collect(Collectors.toList()));
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

	public Optional<String> resolveToken(HttpServletRequest request) {
		String token = request.getHeader(ACCESS_TOKEN_HEADER_NAME);

		if (StringUtils.hasText(token) && token.startsWith(AUTHENTICATION_TYPE_BEARER)) {
			return Optional.of(token.substring(7));
		}
		return Optional.empty();

	}

}
