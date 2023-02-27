package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.job.entity.JobGroup;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

	@Query(
		"select bs.id as id, bs.name as name, b.id as bookId,b.title as title, b.imageUrl as imageUrl from Bookshelf bs join bs.user u "
			+ "join u.job j join bs.bookshelfItems i join  i.book b where j.jobGroup = :jobGroup and u.id <> :user")
	List<response> findAllByJob(@Param("jobGroup") JobGroup jobGroup, Pageable pageable, Long user);

	interface response {
		Long getId();

		String getName();

		List<BookResponse> getBook();

		interface BookResponse {
			Long getBookId();

			String getTitle();

			String getImageUrl();
		}

	}
}
