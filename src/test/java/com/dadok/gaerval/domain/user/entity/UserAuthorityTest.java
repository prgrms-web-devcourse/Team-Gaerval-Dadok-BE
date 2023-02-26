package com.dadok.gaerval.domain.user.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserAuthorityTest {

	@DisplayName("create - Role로 UserAuthority 생성에 성공한다.")
	@Test
	void create_withRole_success() {
		//given
		Role user = Role.USER;

		//when
		UserAuthority userAuthority = UserAuthority.create(user);

		//then
		assertEquals(userAuthority.getAuthority().getName(), user);
	}

	@DisplayName("create - Authority로 UserAuthority 생성에 성공한다.")
	@Test
	void create_withAuthority_success() {
		//given
		Role user = Role.USER;
		Authority authority = Authority.create(user);
		//when
		UserAuthority userAuthority = UserAuthority.create(authority);
		//then
		assertEquals(userAuthority.getAuthority(), authority);
	}

}
