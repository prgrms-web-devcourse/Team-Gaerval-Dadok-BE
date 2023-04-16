package com.dadok.gaerval.domain.bookshelf.entity;

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

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Bookshelf_likes",
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

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "bookshelf_id")
	private Bookshelf bookshelf;

	private BookshelfLike(User user, Bookshelf bookshelf) {
		Assert.notNull(bookshelf, "BookshelfLike의 bookshelf는 null일 수 없습니다.");
		Assert.notNull(user, "BookshelfLike의 user은 null일 수 없습니다.");
		this.user = user;
		this.bookshelf = bookshelf;
	}

	public static BookshelfLike create(User user, Bookshelf bookshelf) {
		return new BookshelfLike(user, bookshelf);
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
