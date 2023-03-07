package com.dadok.gaerval.domain.book_group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.dadok.gaerval.domain.book_group.entity.BookGroup;

public interface BookGroupRepository extends JpaRepository<BookGroup, Long>, BookGroupSupport {

	@Query("SELECT bg FROM BookGroup bg LEFT JOIN FETCH bg.groupMembers WHERE bg.id = :groupId")
	Optional<BookGroup> findByIdWithGroupMembers(Long groupId);

}
