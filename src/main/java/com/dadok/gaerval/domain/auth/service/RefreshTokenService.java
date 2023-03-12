package com.dadok.gaerval.domain.auth.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.dadok.gaerval.domain.auth.token.RefreshToken;
import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.domain.auth.repository.RefreshTokenRepository;
import com.dadok.gaerval.domain.auth.util.RefreshTokenProperties;
import com.dadok.gaerval.global.util.UUIDGenerator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

	private final RefreshTokenRepository refreshTokenRepository;

	private final UUIDGenerator uuidGenerator;

	private final RefreshTokenProperties refreshTokenProperties;

	@Transactional
	public RefreshToken createRefreshToken(Long userId, Collection<GrantedAuthority> authorities) {
		RefreshToken refreshToken = new RefreshToken(uuidGenerator.generateToString(), userId,
			refreshTokenProperties.getExpirationSeconds(),
			authorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList()));

		return refreshTokenRepository.save(refreshToken);
	}

	@Transactional(readOnly = true)
	public RefreshToken getBy(String refreshToken) {
		if (!StringUtils.hasText(refreshToken)) {
			throw new RefreshTokenAuthenticationNotFoundException();
		}

		return refreshTokenRepository.findById(refreshToken)
			.orElseThrow(RefreshTokenAuthenticationNotFoundException::new);
	}

	@Transactional
	public void removeRefreshToken(String refreshToken) {
		refreshTokenRepository.deleteById(refreshToken);
	}

}
