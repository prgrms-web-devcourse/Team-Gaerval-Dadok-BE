package com.dadok.gaerval.domain.user.service;

import java.util.Optional;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

public interface UserService {

	Optional<User> findByEmailWithAuthorities(String email);

	User register(OAuth2Attribute attribute);

}
