package com.dadok.gaerval.domain.user.api;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.user.dto.request.UserJobRegisterRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobRegisterResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.config.security.UserPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailResponse> userMe(@AuthenticationPrincipal UserPrincipal userPrincipal) {
		return ResponseEntity.ok(userService.getUserDetail(userPrincipal.getUserId()));
	}

	@PreAuthorize("hasAuthority('ROLE_ADMIN') or (hasAuthority('ROLE_USER') and #userId == #userPrincipal.userId )")
	@PatchMapping(value = "/{userId}/jobs", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserJobRegisterResponse> registerUserJob(
		@PathVariable Long userId,
		@RequestBody @Valid UserJobRegisterRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {

		UserJobRegisterResponse userJobRegisterResponse = userService.registerJob(userId, request);
		return ResponseEntity.ok(userJobRegisterResponse);
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/{userId}/profile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserProfileResponse> findUserProfile(
		@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(userService.getUserProfile(userId));
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_USER')")
	@PutMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailResponse> findUserProfile(
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		return ResponseEntity.ok(userService.changeProfile(userPrincipal.getUserId()));
	}

}
