package com.dadok.gaerval.domain.book_group.entity;

import static com.dadok.gaerval.global.util.CommonValidator.*;

import java.time.LocalDate;
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

import org.springframework.security.crypto.password.PasswordEncoder;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.book_group.exception.AlreadyContainBookGroupException;
import com.dadok.gaerval.domain.book_group.exception.ExceedMaximumNumberOfMemberException;
import com.dadok.gaerval.domain.book_group.exception.JoinLimitException;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedPasswordException;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "book_groups",
	indexes = {@Index(name = "owner_id_index", columnList = "owner_id"),
		@Index(name = "is_public_index", columnList = "is_public")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookGroup extends BaseTimeColumn {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "owner_id")
	private Long ownerId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Book book;

	@Column(nullable = false, length = 30)
	private String title;

	@Column(nullable = false)
	private LocalDate startDate;

	@Column(nullable = false)
	private LocalDate endDate;

	@Column(nullable = false)
	private Integer maxMemberCount;

	@Column(nullable = false, length = 1000)
	private String introduce;

	@Column(nullable = false, name = "is_public")
	private Boolean isPublic;

	@Column(nullable = false, name = "has_join_passwd")
	private boolean hasJoinPasswd = false;

	public boolean hasJoinPasswd() {
		return this.hasJoinPasswd;
	}

	@Column(length = 30)
	private String joinQuestion;

	@Column(length = 64)
	private String joinPasswd;

	@OneToMany(mappedBy = "bookGroup", cascade = CascadeType.ALL, orphanRemoval = true)
	private final List<GroupMember> groupMembers = new ArrayList<>();

	@OneToMany(mappedBy = "bookGroup", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<GroupComment> comments = new ArrayList<>();

	protected BookGroup(Long ownerId, Book book, LocalDate startDate, LocalDate endDate, Integer maxMemberCount,
		String introduce, Boolean hasJoinPasswd, String title, String joinQuestion, String joinPasswd,
		Boolean isPublic, PasswordEncoder passwordEncoder) {
		validateNotnull(ownerId, "ownerId");
		validateLengthLessThen(title, 30, "title");
		validateNotnull(book, "book");
		CommonValidator.validatePeriod(startDate, endDate);
		validateMaxMemberCount(maxMemberCount);
		validateLengthLessThen(introduce, 1000, "introduce");
		validateHasJoinPasswd(hasJoinPasswd, joinQuestion, joinPasswd);
		validateNotnull(isPublic, "isPublic");
		this.ownerId = ownerId;
		this.book = book;
		this.startDate = startDate;
		this.endDate = endDate;
		this.maxMemberCount = maxMemberCount;
		this.introduce = introduce;
		this.hasJoinPasswd = hasJoinPasswd;
		this.title = title;
		this.joinQuestion = joinQuestion;
		this.isPublic = isPublic;
		this.joinPasswd = Boolean.TRUE.equals(hasJoinPasswd) ? passwordEncoder.encode(joinPasswd) : null;
	}

	private void validateHasJoinPasswd(Boolean hasJoinPasswd, String joinQuestion, String joinPasswd) {
		if (Boolean.TRUE.equals(hasJoinPasswd)) {
			validateLengthLessThen(joinQuestion, 30, "joinQuestion");
			validateWhiteSpaceAndLengthLessThen(joinPasswd, 10, "joinPasswd");
		}
	}

	private void validateMaxMemberCount(Integer maxMemberCount) {
		validateNotnull(maxMemberCount, "maxMemberCount");
		Assert.isTrue(maxMemberCount >= 1, "모임의 최대 인원수는 자신 포함 1명 이상이여야합니다.");
	}

	public static BookGroup create(Long ownerId, Book book, LocalDate startDate, LocalDate endDate,
		Integer maxMemberCount, String title, String introduce, Boolean hasJoinPasswd, String joinQuestion,
		String joinPasswd, Boolean isPublic, PasswordEncoder passwordEncoder) {

		return new BookGroup(ownerId, book, startDate, endDate, maxMemberCount, introduce, hasJoinPasswd, title,
			joinQuestion, joinPasswd, isPublic, passwordEncoder);
	}

	public void addComment(GroupComment groupComment) {
		validateNotnull(groupComment, "groupComment");
		if (!this.comments.contains(groupComment)) {
			this.comments.add(groupComment);
		}
	}

	public void addMember(GroupMember groupMember) {
		validateNotnull(groupMember, "groupMember");
		if (this.groupMembers.contains(groupMember)) {
			throw new AlreadyContainBookGroupException();
		}
		if (this.groupMembers.size() >= this.maxMemberCount) {
			throw new ExceedMaximumNumberOfMemberException();
		}
		this.groupMembers.add(groupMember);
	}

	@JacocoExcludeGenerated
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BookGroup bookGroup = (BookGroup)o;
		return Objects.equals(id, bookGroup.id) && Objects.equals(ownerId, bookGroup.ownerId)
			&& Objects.equals(book, bookGroup.book) && Objects.equals(title, bookGroup.title)
			&& Objects.equals(startDate, bookGroup.startDate) && Objects.equals(endDate,
			bookGroup.endDate) && Objects.equals(maxMemberCount, bookGroup.maxMemberCount)
			&& Objects.equals(introduce, bookGroup.introduce) && Objects.equals(isPublic,
			bookGroup.isPublic) && Objects.equals(hasJoinPasswd, bookGroup.hasJoinPasswd)
			&& Objects.equals(joinQuestion, bookGroup.joinQuestion) && Objects.equals(joinPasswd,
			bookGroup.joinPasswd);
	}

	@JacocoExcludeGenerated
	@Override
	public int hashCode() {
		return Objects.hash(id, ownerId, book, title, startDate, endDate, maxMemberCount, introduce, isPublic,
			hasJoinPasswd, joinQuestion, joinPasswd);
	}

	public void checkPasswd(String joinPassword, PasswordEncoder passwordEncoder) {
		if (this.hasJoinPasswd && !passwordEncoder.matches(this.joinPasswd, joinPassword)) {
			throw new NotMatchedPasswordException();
		}
	}

	public void checkMemberCount() {
		if (this.maxMemberCount == this.getGroupMembers().size()) {
			throw new JoinLimitException();
		}
	}
}
