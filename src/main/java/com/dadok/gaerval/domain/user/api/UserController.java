package com.dadok.gaerval.domain.user.api;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Range;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.user.dto.request.NicknameChangeRequest;
import com.dadok.gaerval.domain.user.dto.request.UserChangeProfileRequest;
import com.dadok.gaerval.domain.user.dto.request.UserJobChangeRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobChangeResponse;
import com.dadok.gaerval.domain.user.dto.response.UserNicknameExistsResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.config.security.UserPrincipal;
import com.dadok.gaerval.global.util.RegexHelper;

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
	public ResponseEntity<UserJobChangeResponse> registerUserJob(
		@PathVariable Long userId,
		@RequestBody @Valid UserJobChangeRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal) {

		UserJobChangeResponse userJobChangeResponse = userService.changeJob(userId, request);
		return ResponseEntity.ok(userJobChangeResponse);
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_ANONYMOUS')")
	@GetMapping(value = "/{userId}/profile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserProfileResponse> findUserProfile(
		@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(userService.getUserProfile(userId));
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_USER')")
	@PutMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailResponse> changeProfile(
		@RequestBody @Valid UserChangeProfileRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		return ResponseEntity.ok(userService.changeProfile(userPrincipal.getUserId(), request));
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
	@GetMapping(value = "/nickname", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserNicknameExistsResponse> existsUsername(
		@RequestParam @Valid
		@NotBlank
		@Pattern(regexp = RegexHelper.NICKNAME_REGEX, message = "특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용합니다.")
		String nickname) {

		return ResponseEntity.ok(new UserNicknameExistsResponse(userService.existsNickname(new Nickname(nickname))));
	}

	@PatchMapping(value = "/profile/nickname", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserDetailResponse> changeProfile(
		@RequestBody @Valid NicknameChangeRequest request,
		@AuthenticationPrincipal UserPrincipal userPrincipal
	) {
		userService.changeNickname(userPrincipal.getUserId(), new Nickname(request.getNickname()));
		return ResponseEntity.ok().build();
	}

	@PreAuthorize(value = "hasAnyRole('ROLE_ADMIN', 'ROLE_USER', 'ROLE_ANONYMOUS')")
	@GetMapping(value = "/profile", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<UserProfileResponses> searchAllByNickname(
		@RequestParam @Valid @NotBlank
		@Pattern(regexp = RegexHelper.NICKNAME_REGEX, message = "특수문자와 공백을 제외한 한글, 숫자, 영어 2~10글자만 허용합니다.")
		String nickname,
		@RequestParam(required = false, defaultValue = "10") @Valid @Range(min = 1, max = 30, message = "최소 {min} 자 이상, {max} 자 이하 여야 합니다")
		int pageSize) {

		return ResponseEntity.ok(userService.searchAllByNickname(new Nickname(nickname), pageSize));
	}

}
