package com.dadok.gaerval.domain.book_group.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book_group.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
}
