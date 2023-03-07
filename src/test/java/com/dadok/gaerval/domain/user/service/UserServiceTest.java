package com.dadok.gaerval.domain.user.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.repository.AuthorityRepository;
import com.dadok.gaerval.integration_util.ServiceIntegration;

import lombok.extern.slf4j.Slf4j;
@TestPropertySource(properties = {"spring.config.location=classpath:application-test.yml"})
@Slf4j
class UserServiceTest extends ServiceIntegration {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Test
	void findByEmailWithAuthorities() {
		List<Authority> all = authorityRepository.findAll();

		log.debug("hihi");
	}

	@Test
	void register() {

		log.debug("hihi2");
	}

	@Test
	void findById() {
	}

	@Test
	void getById() {
	}

	@Test
	void getUserProfile() {
	}

	@Test
	void getUserDetail() {
	}

	@Test
	void changeJob() {
	}

	@Test
	void changeProfile() {
	}

	@Test
	void existsNickname() {
	}

	@Test
	void changeNickname() {
	}
}