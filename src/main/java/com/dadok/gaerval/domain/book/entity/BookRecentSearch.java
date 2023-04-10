package com.dadok.gaerval.domain.book.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="book_recent_searchs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookRecentSearch extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id",nullable = false)
	private User user;

	@Column(nullable = false)
	@Size(min = 1)
	private String keyword;

	protected BookRecentSearch(User user, String keyword) {
		this.user = user;
		this.keyword = keyword;
		CommonValidator.validateLengthGraterThen(keyword, 1, "검색어는 한글자 이상이어야 합니다.");
	}

	public static BookRecentSearch create(User user, String keyword) {
		return new BookRecentSearch(user, keyword);
	}

}
