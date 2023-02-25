package com.dadok.gaerval.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dadok.gaerval.domain.user.entity.Authority;
import com.dadok.gaerval.domain.user.entity.Role;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Role> {
}
