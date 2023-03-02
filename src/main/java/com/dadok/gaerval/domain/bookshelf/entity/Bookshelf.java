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
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.util.Assert;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "bookshelves")
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

	private Bookshelf(Boolean isPublic, User user) {
		Assert.notNull(isPublic, "Bookshelf의 isPublic null일 수 없습니다.");
		Assert.notNull(user, "Bookshelf의 user는 null일 수 없습니다.");

		this.name = user.getOauthNickname() + "님의 책장";
		this.isPublic = isPublic;
		this.user = user;
	}

	private void validateName(String name) {
		Assert.notNull(name, "Bookshelf의 name은 null일 수 없습니다.");
		Assert.isTrue(name.length() <= 30, "Bookshelf의 name은 30자 이하여야합니다.");
	}

	public static Bookshelf create(User user) {
		return new Bookshelf(true, user);
	}

	public void addBookShelfItem(BookshelfItem bookshelfItem) {
		Assert.notNull(bookshelfItem, "BookshelfItem 추가시 데이터가 null일 수 없습니다.");
		if (bookshelfItems.contains(bookshelfItem)) {
			throw new AlreadyContainBookshelfItemException();
		}
		bookshelfItems.add(bookshelfItem);
	}

	public void changeName(String name) {
		validateName(name);
		this.name = name;
	}

	public void changeIsPublic(Boolean isPublic) {
		Assert.notNull(isPublic, "Bookshelf의 isPublic null일 수 없습니다.");
		this.isPublic = isPublic;
	}

	public void validateOwner(Long userId) {
		if (!Objects.equals(userId, user.getId())) {
			throw new BookshelfUserNotMatchedException();
		}
	}
}
