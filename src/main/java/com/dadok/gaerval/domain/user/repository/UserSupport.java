package com.dadok.gaerval.domain.user.repository;

import org.springframework.data.domain.Pageable;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponses;
import com.dadok.gaerval.domain.user.vo.Nickname;

public interface UserSupport {

	UserDetailResponse findUserDetail(Long userId);

	UserProfileResponse findUserProfile(Long userId);

	boolean existsByNickname(Nickname nickname);

	UserProfileResponses findAllByNickname(Nickname nickname, Pageable pageable);

}
