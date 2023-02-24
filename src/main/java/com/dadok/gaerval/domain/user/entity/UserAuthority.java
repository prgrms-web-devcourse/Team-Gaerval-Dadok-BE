package com.dadok.gaerval.domain.user.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "user_authorities",
	uniqueConstraints = {
		@UniqueConstraint(name = "userId_authority_unique_key",
			columnNames = {"user_id", "authority"})
	})
@Entity(name = "user_authorities")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAuthority {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@JsonBackReference
	@ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
	@JoinColumn(name = "authority", nullable = false)
	private Authority authority;

	public Role getAuthorityName() {
		return this.authority.getName();
	}

	protected UserAuthority(Authority authority) {
		this.authority = authority;
	}

	public UserAuthority(User user, Authority authority) {
		this.user = user;
		this.authority = authority;
	}

	public void changeUser(User user) {
		this.user = user;
	}

	public static UserAuthority of(Authority authority) {
		return new UserAuthority(authority);
	}

	public static UserAuthority of(Role role) {
		Authority authority = Authority.of(role);

		return new UserAuthority(authority);
	}

}