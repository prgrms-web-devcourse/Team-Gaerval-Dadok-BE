package com.dadok.gaerval.domain.book_group.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_member")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private BookGroup bookGroup;

	protected GroupMember(User user, BookGroup bookGroup) {
		validateNotnull(user, "user");
		validateNotnull(bookGroup, "bookGroup");

		this.user = user;
		this.bookGroup = bookGroup;
	}

	public static GroupMember create(BookGroup bookGroup, User user) {
		GroupMember groupMember = new GroupMember(user, bookGroup);

		bookGroup.addMember(groupMember);
		return groupMember;
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GroupMember that = (GroupMember)o;
		return Objects.equals(user, that.user) && Objects.equals(bookGroup, that.bookGroup);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(user, bookGroup);
	}
}
