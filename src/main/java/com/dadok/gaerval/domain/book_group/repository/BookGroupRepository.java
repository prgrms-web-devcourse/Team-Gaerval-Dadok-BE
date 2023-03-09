package com.dadok.gaerval.domain.book_group.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public interface BookGroupRepository extends JpaRepository<BookGroup, Long>, BookGroupSupport {

	@Query("SELECT bg FROM BookGroup bg LEFT JOIN FETCH bg.groupMembers WHERE bg.id = :groupId")
	Optional<BookGroup> findByIdWithGroupMembers(Long groupId);


	@Lock(LockModeType.PESSIMISTIC_WRITE)
	// @QueryHints(@QueryHint(name = "javax.persistence.lock.timeout", value = "3000"))
	@Query("SELECT bg FROM BookGroup bg LEFT JOIN FETCH bg.groupMembers WHERE bg.id = :groupId")
	Optional<BookGroup> findByIdWithGroupMembersForUpdate(@Param("groupId") Long groupId);

}
