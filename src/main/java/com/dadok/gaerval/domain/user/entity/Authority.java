package com.dadok.gaerval.domain.user.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "authorities")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority extends BaseTimeColumn {

	@Id
	@Enumerated(EnumType.STRING)
	private Role name;

	protected Authority(Role role) {
		this.name = role;
	}

	public static Authority create(Role role) {
		CommonValidator.validateNotnull(role, "role");

		return new Authority(role);
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Authority authority = (Authority)o;
		return name == authority.name;
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

