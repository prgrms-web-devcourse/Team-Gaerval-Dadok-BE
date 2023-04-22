package com.dadok.gaerval.domain.user.service;

import java.util.Optional;

import com.dadok.gaerval.domain.user.dto.request.UserChangeProfileRequest;
import com.dadok.gaerval.domain.user.dto.request.UserJobChangeRequest;
import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserJobChangeResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.vo.Nickname;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

public interface UserService {

	Optional<User> findByEmailWithAuthorities(String email);

	User register(OAuth2Attribute attribute);

	Optional<User> findById(Long userId);

	User getById(Long getUserId);

	UserProfileResponse getUserProfile(Long userId);

	UserDetailResponse getUserDetail(Long userId);

	UserJobChangeResponse changeJob(Long userId, UserJobChangeRequest request);

	UserDetailResponse changeProfile(Long userId, UserChangeProfileRequest request);

	boolean existsNickname(Nickname nickname);

	void changeNickname(Long userId, Nickname nickname);

	UserProfileResponses searchAllByNickname(Nickname nickname, int pageSize);

}
