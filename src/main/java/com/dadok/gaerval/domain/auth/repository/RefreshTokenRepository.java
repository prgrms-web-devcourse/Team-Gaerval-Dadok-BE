package com.dadok.gaerval.domain.auth.repository;

import org.springframework.data.repository.CrudRepository;

import com.dadok.gaerval.domain.auth.token.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
