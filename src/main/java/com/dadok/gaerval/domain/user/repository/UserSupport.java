package com.dadok.gaerval.domain.user.repository;

import com.dadok.gaerval.domain.user.dto.response.UserDetailResponse;

public interface UserSupport {

	UserDetailResponse findUserDetail(Long userId);

}
