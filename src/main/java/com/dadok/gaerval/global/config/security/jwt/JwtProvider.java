package com.dadok.gaerval.global.config.security.jwt;

import static com.dadok.gaerval.global.error.ErrorCode.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

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
		} catch (ExpiredJwtException | SecurityException
				 | MalformedJwtException | UnsupportedJwtException |
				 IllegalArgumentException e) {
			//todo : 로깅 AOP
			log.warn("error : {}", e);
			throw new JwtAuthenticationException(INVALID_ACCESS_TOKEN, e);
		}
	}

	public Long getId(String accessToken) {
		Claims claims = parse(accessToken);

		return claims.get("id", Long.class);
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

}
