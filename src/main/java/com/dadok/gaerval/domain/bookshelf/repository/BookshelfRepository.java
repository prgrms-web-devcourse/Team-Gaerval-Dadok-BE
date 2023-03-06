package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.job.entity.JobGroup;

public interface BookshelfRepository extends JpaRepository<Bookshelf, Long>, BookshelfSupport {

	@Query("""
		select bs from Bookshelf bs join fetch bs.user u join fetch u.job j left join fetch bs.bookshelfItems i left join fetch  i.book b 
		 where j.jobGroup = :jobGroup and u.id <> :user
		""")
	List<Bookshelf> findAllByJob(@Param("jobGroup") JobGroup jobGroup, Pageable pageable, @Param("user") Long user);

}
