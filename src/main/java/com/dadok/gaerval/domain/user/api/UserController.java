package com.dadok.gaerval.domain.user.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping("/me")
	public ResponseEntity<UserDetailResponse> userMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return ResponseEntity.ok(userService.getUserDetail(userPrincipal.getUserId()));
	}

}