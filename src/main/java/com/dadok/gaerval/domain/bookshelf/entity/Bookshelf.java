package com.dadok.gaerval.domain.bookshelf.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookshelves", indexes = @Index(name = "job_id_index", columnList = "job_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookshelf extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 30)
	private String name;

	@Column(nullable = false)
	private Boolean isPublic;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "bookshelf", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<BookshelfItem> bookshelfItems = new ArrayList<>();

	@Column(name = "job_id", nullable = false)
	private Long jobId;

	private Bookshelf(Boolean isPublic, User user) {
		CommonValidator.validateNotnull(isPublic, "isPublic");
		CommonValidator.validateNotnull(user, "user");
		CommonValidator.validateNotnull(user.getJob().getId(), "jobId");
		this.name = user.getOauthNickname() + "님의 책장";
		this.isPublic = isPublic;
		this.user = user;
		this.jobId = user.getJob().getId();
	}

	public static Bookshelf create(User user) {
		return new Bookshelf(true, user);
	}

	public void addBookShelfItem(BookshelfItem bookshelfItem) {
		CommonValidator.validateNotnull(bookshelfItem, "bookShelfItem");

		if (bookshelfItems.contains(bookshelfItem)) {
			throw new AlreadyContainBookshelfItemException();
		}
		bookshelfItems.add(bookshelfItem);
	}

	public void changeName(String name) {
		CommonValidator.validateLengthLessThen(name, 30, "name");
		this.name = name;
	}

	public void changeIsPublic(Boolean isPublic) {
		CommonValidator.validateNotnull(isPublic, "isPublic");
		this.isPublic = isPublic;
	}

	public void validateOwner(Long userId) {
		if (!Objects.equals(userId, user.getId())) {
			throw new BookshelfUserNotMatchedException();
		}
	}
}
