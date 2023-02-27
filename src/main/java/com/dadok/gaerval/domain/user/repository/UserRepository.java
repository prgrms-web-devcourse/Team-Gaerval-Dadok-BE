package com.dadok.gaerval.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.user.entity.User;

public interface UserRepository extends JpaRepository<User, Long>,
	UserSupport {

	Optional<User> findTopByEmail(@Param("email") String email);

	@Query(value = """
				SELECT u FROM User u
				left join fetch u.authorities ua
				left join fetch ua.authority
				WHERE u.email = :email
		""")
	Optional<User> findTopByEmailWithAuthorities(@Param("email") String email);

	@Query(value = """
		SELECT u FROM User u
		left join fetch u.authorities ua
		left join fetch ua.authority
		WHERE u.id = :id""")
	Optional<User> findByIdWithAuthorities(@Param("id") Long id);

}
