package com.dadok.gaerval.domain.user.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String nickname;

	@Email
	@Column(unique = true, nullable = true)
	private String email;

	private String profileImage;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private AuthProvider authProvider;

	private String authId;

	@JsonManagedReference
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private List<UserAuthority> authorities = new ArrayList<>();

	@Builder
	protected User(String name, String nickname, String email, String profileImage, Gender gender,
		AuthProvider authProvider, String authId, UserAuthority userAuthority) {
		this.name = name;
		this.nickname = nickname;
		this.email = email;
		this.profileImage = profileImage;
		this.gender = gender;
		this.authorities.add(userAuthority);
		this.authProvider = authProvider;
		this.authId = authId;
		userAuthority.changeUser(this);
	}

	public static User createByOAuth(OAuth2Attribute attribute, UserAuthority userAuthority) {
		return User.builder()
			.email(attribute.getEmail())
			.nickname(attribute.getName())
			.authProvider(attribute.getAuthProvider())
			.authId(attribute.getOauthId())
			.profileImage(attribute.getPicture())
			.userAuthority(userAuthority)
			.build();
	}

	public Collection<GrantedAuthority> grantedAuthorities() {
		return this.authorities.stream()
			.map(userAuthority ->
				new SimpleGrantedAuthority(userAuthority.getAuthorityName().getAuthority()))
			.collect(Collectors.toList());
	}

	public boolean hasJob() {
		return false;
	}

}
