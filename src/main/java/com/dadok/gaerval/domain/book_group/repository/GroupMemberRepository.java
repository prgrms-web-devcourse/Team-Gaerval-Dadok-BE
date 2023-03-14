package com.dadok.gaerval.domain.book_group.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dadok.gaerval.domain.book_group.entity.GroupMember;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

	boolean existsByBookGroupIdAndUserId(Long bookGroupId, Long userId);

	Optional<GroupMember> findByBookGroupIdAndUserId(Long groupId, Long userId);
}
