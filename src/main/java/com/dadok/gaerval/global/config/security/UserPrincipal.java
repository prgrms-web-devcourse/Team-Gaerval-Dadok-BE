package com.dadok.gaerval.global.config.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dadok.gaerval.domain.user.entity.User;

public class UserPrincipal
	extends org.springframework.security.core.userdetails.User
	implements OAuth2User {

	private Long userId;

	private String accessToken;

	private Collection<GrantedAuthority> authorities;

	private Map<String, Object> attributes;

	private UserPrincipal(Long userId, Collection<GrantedAuthority> authorities) {
		super(userId.toString(), "", authorities);
		this.userId = userId;
		this.authorities = authorities;
	}

	private UserPrincipal(Long userId, String accessToken, Collection<GrantedAuthority> authorities) {
		super(userId.toString(), "", authorities);
		this.userId = userId;
		this.authorities = authorities;
		this.accessToken = accessToken;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public static UserPrincipal of(User userEntity) {
		return new UserPrincipal(userEntity.getId(), userEntity.grantedAuthorities());
	}

	public static UserPrincipal of(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = of(user);
		userPrincipal.setAttributes(attributes);

		return userPrincipal;
	}

	public static UserPrincipal of(Long userId, String accessToken, Collection<GrantedAuthority> authorities) {
		return new UserPrincipal(userId, accessToken, authorities);
	}

	public void setAttributes(Map<String, Object> attributes) {
		this.attributes = attributes;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return this.attributes;
	}

	@Override
	public String getName() {
		return this.userId.toString();
	}

	public Long getUserId() {
		return this.userId;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

}
