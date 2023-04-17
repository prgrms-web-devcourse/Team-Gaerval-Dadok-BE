package com.dadok.gaerval.domain.bookshelf.entity;

import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "bookshelf_likes")
@Table(
	uniqueConstraints = {
		@UniqueConstraint(name = "bookshelf_id_user_id_unique_key",
			columnNames = {"bookshelf_id", "user_id"})
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookshelfLike extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "bookshelf_id")
	private Bookshelf bookshelf;

	private BookshelfLike(User user, Bookshelf bookshelf) {
		CommonValidator.validateNotnull(bookshelf, "bookshelf");
		CommonValidator.validateNotnull(user, "user");
		validateNotOwner(user, bookshelf);
		this.user = user;
		this.bookshelf = bookshelf;
		bookshelf.addBookShelfLike(this);
	}

	public static BookshelfLike create(User user, Bookshelf bookshelf) {
		return new BookshelfLike(user, bookshelf);
	}

	private void validateNotOwner(User user, Bookshelf bookshelf) {
		if (Objects.equals(bookshelf.getUser().getId(), user.getId())) {
			throw new BookshelfUserNotMatchedException();
		}
	}

	@Override
	@JacocoExcludeGenerated
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BookshelfLike that = (BookshelfLike)o;
		return user.equals(that.user) && bookshelf.equals(that.bookshelf);
	}

	@Override
	@JacocoExcludeGenerated
	public int hashCode() {
		return Objects.hash(user, bookshelf);
	}
}
