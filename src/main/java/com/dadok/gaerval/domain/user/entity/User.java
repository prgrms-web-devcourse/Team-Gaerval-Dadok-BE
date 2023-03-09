package com.dadok.gaerval.domain.user.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.springframework.security.core.GrantedAuthority;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;
import com.dadok.gaerval.global.util.CommonValidator;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 30)
	private String name;

	@Column(length = 30, unique = true, name = "nickname", nullable = true)
	@Embedded
	private Nickname nickname;

	@Column(length = 100)
	private String oauthNickname;

	@Email
	@Column(unique = true, length = 255, nullable = true)
	private String email;

	@Column(length = 500)
	private String profileImage;

	@Enumerated(EnumType.STRING)
	@Column(length = 10)
	private Gender gender;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private AuthProvider authProvider;

	@Column(nullable = false, length = 255)
	private String authId;

	private LocalDateTime birthday;

	@JsonManagedReference
	@OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<UserAuthority> authorities = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY, cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH})
	@JoinColumn(name = "job_id")
	private Job job;

	@Builder
	protected User(String oauthNickname, String email, String profileImage, Gender gender,
		AuthProvider authProvider, String authId, UserAuthority userAuthority) {
		CommonValidator.validateLengthLessThen(oauthNickname, 100, "oauthNickname");

		CommonValidator.validateEmail(email);
		CommonValidator.validateNotnull(gender, "gender");
		CommonValidator.validateNotnull(authProvider, "authProvider");
		CommonValidator.validateNotnull(userAuthority, "userAuthority");

		this.oauthNickname = oauthNickname;
		this.email = email;
		this.profileImage = profileImage;
		this.gender = gender;
		this.authProvider = authProvider;
		this.authId = authId;
		this.authorities.add(userAuthority);
		userAuthority.changeUser(this);
	}

	public static User createByOAuth(OAuth2Attribute attribute, UserAuthority userAuthority) {

		String gender = (String)attribute.getAttributes().get("gender");

		return User.builder()
			.email(attribute.getEmail())
			.oauthNickname(attribute.getName())
			.authProvider(attribute.getAuthProvider())
			.authId(attribute.getOauthId())
			.profileImage(attribute.getPicture())
			.userAuthority(userAuthority)
			.gender(gender == null ? Gender.NONE : Gender.of(gender))
			.build();
	}

	public Collection<GrantedAuthority> grantedAuthorities() {
		return this.authorities.stream()
			.map(UserAuthority::getAuthorityName)
			.collect(Collectors.toList());
	}

	public void changeJob(Job job) {
		CommonValidator.validateNotnull(job, "job");
		this.job = job;
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		User user = (User)o;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name)
			&& Objects.equals(nickname, user.nickname) && Objects.equals(email, user.email)
			&& Objects.equals(profileImage, user.profileImage) && gender == user.gender
			&& authProvider == user.authProvider && Objects.equals(authId, user.authId)
			&& Objects.equals(birthday, user.birthday) && Objects.equals(authorities, user.authorities);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(id, name, nickname, email, profileImage, gender, authProvider, authId, birthday,
			authorities);
	}

	public void changeNickname(Nickname nickname) {
		CommonValidator.validateNotnull(nickname, "nickname");
		this.nickname = nickname;
	}

	public boolean isSameNickname(Nickname nickname) {
		CommonValidator.validateNotnull(nickname, "nickname");
		return Objects.equals(this.nickname, nickname);
	}

	public boolean isSameJob(Job job) {
		CommonValidator.validateNotnull(job, "job");
		return Objects.equals(this.job, job);
	}
}
