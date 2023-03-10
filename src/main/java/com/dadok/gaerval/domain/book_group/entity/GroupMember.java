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
import javax.persistence.UniqueConstraint;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.TimeHolder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_member",
	uniqueConstraints = {
		@UniqueConstraint(name = "userId_authority_unique_key",
			columnNames = {"user_id", "book_group_id"})
	})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupMember extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "book_group_id")
	private BookGroup bookGroup;

	protected GroupMember(User user, BookGroup bookGroup) {
		validateNotnull(user, "user");
		validateNotnull(bookGroup, "bookGroup");

		this.user = user;
		this.bookGroup = bookGroup;
	}

	protected GroupMember(User user) {
		validateNotnull(user, "user");
		this.user = user;
	}

	public static GroupMember create(BookGroup bookGroup, User user, TimeHolder timeHolder) {
		GroupMember groupMember = new GroupMember(user, bookGroup);

		bookGroup.addMember(groupMember, timeHolder);
		return groupMember;
	}

	public static GroupMember create(User user) {
		return new GroupMember(user);
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		GroupMember that = (GroupMember)o;
		return Objects.equals(id, that.id) && Objects.equals(user, that.user);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(id, user, bookGroup);
	}

	public void changeGroup(BookGroup bookGroup) {
		if (this.bookGroup != bookGroup) {
			this.bookGroup = bookGroup;
		}
	}
}
