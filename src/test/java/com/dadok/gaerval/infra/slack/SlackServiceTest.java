package com.dadok.gaerval.infra.slack;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@RequiredArgsConstructor
class SlackServiceTest {

	private final SlackService slackService;

	@DisplayName("send test")
	@Test
	void send() {
		//given

		slackService.sendError(new Exception("예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트예외예외 테스트예외 테스트예외 테스트예외 테스트예외 테스트 테스트예외 테스트예외 테스트예외 테스트예외 테스트"), "/api/users/path");
		//then
	}


	void test1() {

	}

}