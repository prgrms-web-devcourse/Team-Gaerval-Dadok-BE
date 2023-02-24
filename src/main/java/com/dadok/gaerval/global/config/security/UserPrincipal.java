package com.dadok.gaerval.global.config.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.dadok.gaerval.domain.user.entity.User;

import lombok.Getter;

public class UserPrincipal
	extends org.springframework.security.core.userdetails.User
	implements OAuth2User {

	@Getter
	private final User userEntity;

	private Map<String, Object> attributes;

	private UserPrincipal(User userEntity) {
		super(userEntity.getEmail(),
			"",
			userEntity.grantedAuthorities());

		this.userEntity = userEntity;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return this.userEntity.grantedAuthorities();
	}

	public static UserPrincipal of(User userEntity) {
		return new UserPrincipal(userEntity);
	}

	public static UserPrincipal of(User user, Map<String, Object> attributes) {
		UserPrincipal userPrincipal = of(user);
		userPrincipal.setAttributes(attributes);

		return userPrincipal;
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
		return userEntity.getEmail();
	}

	public Long getUserId() {
		return this.userEntity.getId();
	}
}
