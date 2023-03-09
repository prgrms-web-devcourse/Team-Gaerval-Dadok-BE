package com.dadok.gaerval.domain.book_group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book_group.entity.GroupComment;

public interface BookGroupCommentRepository extends JpaRepository<GroupComment, Long>, BookGroupCommentSupport {
}
