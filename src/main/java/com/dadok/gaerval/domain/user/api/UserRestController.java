package com.dadok.gaerval.domain.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

	private final UserService userService;

	@GetMapping("/me")
	public ResponseEntity<?> userMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {

		userService.getUserDetail(userPrincipal.getUserId());
		return null;
	}

}
