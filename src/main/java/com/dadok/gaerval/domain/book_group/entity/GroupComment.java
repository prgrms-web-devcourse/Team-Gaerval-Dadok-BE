package com.dadok.gaerval.domain.book_group.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.dadok.gaerval.domain.book_group.exception.InvalidCommentException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "group_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GroupComment extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Column(length = 2000, nullable = false)
	private String contents;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_group_id")
	private BookGroup bookGroup;

	@JsonManagedReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private GroupComment parentComment;

	@JsonBackReference
	@OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY)
	private List<GroupComment> childComments = new ArrayList<>();

	protected GroupComment(String contents, BookGroup bookGroup, User user) {
		validateLengthLessThen(contents, 2000, "contents");
		validateNotnull(bookGroup, "bookGroup");
		validateNotnull(user, "user");

		this.contents = contents;
		this.user = user;
		this.bookGroup = bookGroup;
		bookGroup.addComment(this);
	}

	public static GroupComment create(String contents, BookGroup bookGroup, User user) {
		return new GroupComment(contents, bookGroup, user);
	}

	public static GroupComment createChild(String contents, BookGroup bookGroup, User user, GroupComment parent) {
		GroupComment child = new GroupComment(contents, bookGroup, user);
		child.addParent(parent);

		return child;
	}

	public void addParent(GroupComment parent) {
		validateNotnull(parent, "parentComment");
		if (!parent.isParent()) {
			throw new InvalidCommentException(ErrorCode.INVALID_COMMENT_NOT_PARENT);
		}

		if (!Objects.equals(this.parentComment, parent)) {
			this.parentComment = parent;
			parent.addChild(this);
		}
	}

	public void addChild(GroupComment child) {
		validateNotnull(child, "childComment");
		if (!this.isParent() || child.isParent()) {
			throw new InvalidCommentException(ErrorCode.INVALID_COMMENT_NOT_PARENT);
		}
		if (!this.childComments.contains(child)) {
			this.childComments.add(child);
		}
	}

	public boolean isParent() {
		return this.parentComment == null;
	}

	public boolean isChild() {
		return this.parentComment != null;
	}

	public void changeContents(String contents) {
		validateLengthLessThen(contents, 2000, "contents");
		this.contents = contents;
	}
}
