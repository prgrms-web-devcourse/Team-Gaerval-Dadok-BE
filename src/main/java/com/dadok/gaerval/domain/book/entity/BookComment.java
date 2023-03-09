package com.dadok.gaerval.domain.book.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "book_comments", uniqueConstraints = {
	@UniqueConstraint(name = "user_id_book_id_unique_key",
		columnNames = {"user_id", "book_id"})
})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookComment extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name= "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;

	@Column(name = "comment", nullable = false, length = 500)
	private String comment;


	protected BookComment(User user, Book book, String comment) {
		this.user = user;
		this.book = book;
		this.comment = comment;
		validateLengthInRange(comment, 1, 500, "책 코멘트");
	}

	public static BookComment create(User user, Book book, String comment) {
		return new BookComment(user, book, comment);
	}

	public void changeComment(String comment) {
		this.comment = comment;
		validateLengthInRange(comment, 1, 500, "책 코멘트");
	}
}
