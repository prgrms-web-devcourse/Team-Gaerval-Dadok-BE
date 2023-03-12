package com.dadok.gaerval.global.config.security.jwt;

import static com.dadok.gaerval.global.config.security.jwt.AuthService.*;
import static com.dadok.gaerval.global.error.ErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.dadok.gaerval.global.config.security.UserPrincipal;
import com.dadok.gaerval.global.config.security.exception.ExpiredAccessTokenException;
import com.dadok.gaerval.global.config.security.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtProvider {

	private final JwtProperties jwtProperties;

	private final Key secretKey;

	public JwtProvider(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
		this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes(StandardCharsets.UTF_8));
	}

	public Authentication getAuthentication(String accessToken) {
		Long userId = getId(accessToken);
		Collection<GrantedAuthority> roles = getRoles(accessToken);
		UserPrincipal userPrincipal = UserPrincipal.of(userId, accessToken, roles);

		return new JwtAuthenticationToken(userPrincipal, accessToken);
	}

	public String createAccessToken(Long userId, Collection<GrantedAuthority> authorities) {
		Claims claims = Jwts.claims().setSubject(userId.toString());
		claims.put("id", userId);
		claims.put("roles", authorities.toArray());

		Date expirationTime = accessTokenExpirationTime();

		return Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(new Date())
			.setIssuer(jwtProperties.getIssuer())
			.setExpiration(expirationTime)
			.signWith(secretKey, SignatureAlgorithm.HS256)
			.compact();
	}

	public void validate(String accessToken) {
		try {
			Jwts.parserBuilder()
				.setSigningKey(secretKey)
				.build()
				.parseClaimsJws(accessToken);
		} catch (ExpiredJwtException e) {
			throw new ExpiredAccessTokenException(e);
		} catch (SecurityException
				 | MalformedJwtException
				 | UnsupportedJwtException
				 | IllegalArgumentException e) {
			//todo : 로깅 AOP
			log.warn("error : {}", e.getMessage());
			throw new JwtAuthenticationException(INVALID_ACCESS_TOKEN, e);
		}
	}

	public Long getId(String accessToken) {
		Claims claims = parse(accessToken);

		return claims.get("id", Long.class);
	}

	public Collection<GrantedAuthority> getRoles(String accessToken) {
		Claims claims = parse(accessToken);
		GrantedAuthority[] roles = claims.get("roles", GrantedAuthority[].class);
		return Arrays.stream(roles).toList();
	}

	private Claims parse(String accessToken) {
		return Jwts.parserBuilder()
			.setSigningKey(secretKey)
			.build()
			.parseClaimsJws(accessToken)
			.getBody();
	}

	public Date accessTokenExpirationTime() {
		LocalDateTime expiredDate = LocalDateTime.now().plusSeconds(jwtProperties.getExpirationSecond());

		return Date.from(expiredDate.atZone(ZoneId.systemDefault()).toInstant());
	}

	public static Optional<String> resolveToken(@NotNull HttpServletRequest request) {
		String token = request.getHeader(ACCESS_TOKEN_HEADER_NAME);

		if (StringUtils.hasText(token) && token.startsWith(AUTHENTICATION_TYPE_BEARER)) {
			return Optional.of(token.substring(7));
		}
		return Optional.empty();

	}

}
