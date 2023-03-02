package com.dadok.gaerval.domain.user.repository;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;
import com.dadok.gaerval.domain.user.dto.response.UserProfileResponse;

public interface UserSupport {

	UserDetailResponse findUserDetail(Long userId);

	UserProfileResponse findUserProfile(Long uerId);

}
