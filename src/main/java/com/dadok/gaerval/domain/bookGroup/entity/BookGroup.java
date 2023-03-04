package com.dadok.gaerval.domain.bookGroup.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_group")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookGroup extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long ownerId;

	@OneToOne(fetch = FetchType.LAZY)
	private Book book;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private Integer maxMemberCount;

	@Column(nullable = false, length = 1000)
	private String introduce;

	@Column(nullable = false)
	private Boolean isPublic;

	@OneToMany(mappedBy = "bookGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<GroupMember> groupMembers = new ArrayList<>();

	protected BookGroup(Long ownerId, Book book, LocalDate startDate, LocalDate endDate, Integer maxMemberCount,
		String introduce, Boolean isPublic) {
		validateNotnull(ownerId, "ownerId");
		validateNotnull(book, "book");
		validatePeriod(startDate, endDate);
		validateMaxMemberCount(maxMemberCount);
		validateLengthLessThen(introduce, 1000, "introduce");
		validateNotnull(isPublic, "isPublic");
		this.ownerId = ownerId;
		this.book = book;
		this.startDate = startDate;
		this.endDate = endDate;
		this.maxMemberCount = maxMemberCount;
		this.introduce = introduce;
		this.isPublic = isPublic;
	}

	private void validatePeriod(LocalDate startDate, LocalDate endDate) {
		validateNotnull(startDate, "startDate");
		validateNotnull(endDate, "endDate");
		Assert.isTrue(startDate.isEqual(LocalDate.now()) || startDate.isAfter(LocalDate.now()),
			"모임 시작 날짜는 오늘 부터 가능합니다.");
		Assert.isTrue(endDate.isEqual(startDate) || endDate.isAfter(startDate),
			String.format("모임 종료 날짜는 시작 날짜(%s)부터 가능합니다.", startDate));
	}

	private void validateMaxMemberCount(Integer maxMemberCount) {
		validateNotnull(maxMemberCount, "maxMemberCount");
		Assert.isTrue(maxMemberCount >= 1, "모임의 최대 인원수는 자신 포함 1명 이상이여야합니다.");
	}

	public static BookGroup create(Long ownerId, Book book, LocalDate startDate, LocalDate endDate,
		Integer maxMemberCount,
		String introduce, Boolean isPublic) {
		return new BookGroup(ownerId, book, startDate, endDate, maxMemberCount, introduce, isPublic);
	}
}
