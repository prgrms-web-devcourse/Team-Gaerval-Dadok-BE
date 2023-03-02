package com.dadok.gaerval.domain.user.repository;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;
import com.dadok.gaerval.domain.user.vo.Nickname;

public interface UserSupport {

	UserDetailResponse findUserDetail(Long userId);

	UserProfileResponse findUserProfile(Long userId);

	boolean existsByNickname(Nickname nickname);
}
