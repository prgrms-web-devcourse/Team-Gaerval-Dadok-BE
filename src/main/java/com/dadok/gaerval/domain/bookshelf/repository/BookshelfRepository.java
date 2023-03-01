package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.bookshelf.dto.response.SummaryBookshelfResponse;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long> {

	@Query("""
		select bs.id as bookshelfId, bs.name as bookshelfName, b.id as bookId,b.title as title, b.imageUrl as imageUrl 
		 from Bookshelf bs join bs.user u join u.job j join bs.bookshelfItems i join  i.book b 
		 where j.jobGroup = :jobGroup and u.id <> :user
		""")
	List<SummaryBookshelfResponse> findAllByJob(@Param("jobGroup") JobGroup jobGroup, Pageable pageable, Long user);

	@Query("""
		select bs.id as bookshelfId, bs.name as bookshelfName, b.id as bookId,b.title as title, b.imageUrl as imageUrl 
		 from Bookshelf bs join bs.bookshelfItems i join  i.book b where bs.user = :user
		""")
	SummaryBookshelfResponse findByUser(User user);
}
