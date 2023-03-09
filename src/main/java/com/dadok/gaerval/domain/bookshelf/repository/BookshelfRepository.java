package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long>, BookshelfSupport {

	@Query("SELECT bs FROM Bookshelf bs WHERE bs.user.id IN :userIds  ")
	List<Bookshelf> findAllByUserIds(@Param("userIds") Iterable<Long> userIds);

	@Query("SELECT B FROM Bookshelf B WHERE B.user.id = :userId ")
	Optional<Bookshelf> findByUserId(@Param("userId") Long userId);

}
