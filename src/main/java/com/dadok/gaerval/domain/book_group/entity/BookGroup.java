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
import com.dadok.gaerval.domain.book_group.exception.BookGroupOwnerNotMatchedException;
import com.dadok.gaerval.domain.book_group.exception.CannotDeleteMemberExistException;
import com.dadok.gaerval.domain.book_group.exception.CannotLeaveGroupOwnerException;
import com.dadok.gaerval.domain.book_group.exception.ExceedLimitMemberException;
import com.dadok.gaerval.domain.book_group.exception.ExpiredJoinGroupPeriodException;
import com.dadok.gaerval.domain.book_group.exception.LessThanCurrentMembersException;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedPasswordException;
import com.dadok.gaerval.global.common.JacocoExcludeGenerated;
import com.dadok.gaerval.global.common.entity.BaseTimeColumn;
import com.dadok.gaerval.global.util.CommonValidator;
import com.dadok.gaerval.global.util.TimeHolder;

import io.jsonwebtoken.lang.Assert;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Table(name = "book_groups",
	indexes = {@Index(name = "owner_id_index", columnList = "owner_id"),
		@Index(name = "is_public_index", columnList = "is_public")}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookGroup extends BaseTimeColumn {

	public static final Integer NO_LIMIT_MEMBER_COUNT = 9999;

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

	@OneToMany(mappedBy = "bookGroup", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private final List<GroupMember> groupMembers = new ArrayList<>();

	@OneToMany(mappedBy = "bookGroup", cascade = CascadeType.PERSIST, orphanRemoval = true)
	private List<GroupComment> comments = new ArrayList<>();

	protected BookGroup(Long ownerId, Book book, LocalDate startDate, LocalDate endDate, Integer maxMemberCount,
		String introduce, Boolean hasJoinPasswd, String title, String joinQuestion, String joinPasswd,
		Boolean isPublic, PasswordEncoder passwordEncoder, TimeHolder timeHolder) {
		validateNotnull(ownerId, "ownerId");
		validateLengthLessThen(title, 30, "title");
		validateNotnull(book, "book");
		CommonValidator.validatePeriod(startDate, endDate, timeHolder.getCurrentClock());
		validateMaxMemberCount(maxMemberCount);
		validateLengthLessThen(introduce, 1000, "introduce");
		validateHasJoinPasswd(hasJoinPasswd, joinQuestion, joinPasswd);
		validateNotnull(isPublic, "isPublic");
		this.ownerId = ownerId;
		this.book = book;
		this.startDate = startDate;
		this.endDate = endDate;
		this.maxMemberCount = maxMemberCount == null ? NO_LIMIT_MEMBER_COUNT : maxMemberCount;
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
		if (maxMemberCount != null) {
			Assert.isTrue(maxMemberCount >= 1, "모임의 최대 인원수는 자신 포함 1명 이상이여야합니다.");
			if (maxMemberCount < this.groupMembers.size()) {
				throw new LessThanCurrentMembersException(this.groupMembers.size());
			}
		}
	}

	public static BookGroup create(Long ownerId, Book book, LocalDate startDate, LocalDate endDate,
		Integer maxMemberCount, String title, String introduce, Boolean hasJoinPasswd, String joinQuestion,
		String joinPasswd, Boolean isPublic, PasswordEncoder passwordEncoder, TimeHolder timeHolder) {

		return new BookGroup(ownerId, book, startDate, endDate, maxMemberCount, introduce, hasJoinPasswd, title,
			joinQuestion, joinPasswd, isPublic, passwordEncoder, timeHolder);
	}

	public void addComment(GroupComment groupComment) {
		validateNotnull(groupComment, "groupComment");
		if (!this.comments.contains(groupComment)) {
			this.comments.add(groupComment);
		}
	}

	public void addMember(GroupMember groupMember, TimeHolder timeHolder) {
		validateNotnull(groupMember, "groupMember");
		if (this.groupMembers.contains(groupMember)) {
			throw new AlreadyContainBookGroupException();
		}
		checkCanJoin(timeHolder);
		this.groupMembers.add(groupMember);
		groupMember.changeGroup(this);
	}

	private void checkCanJoin(TimeHolder timeHolder) {
		LocalDate currentDate = LocalDate.now(timeHolder.getCurrentClock());
		if (currentDate.isAfter(this.endDate)) {
			throw new ExpiredJoinGroupPeriodException();
		}
		checkMemberCount();
	}

	public void changeBookGroupContents(String title, String introduce, LocalDate endDate, Integer maxMemberCount,
		TimeHolder timeHolder) {
		validateLengthLessThen(title, 30, "title");
		CommonValidator.validateEndDate(startDate, endDate, timeHolder.getCurrentClock());
		validateMaxMemberCount(maxMemberCount);
		validateLengthLessThen(introduce, 1000, "introduce");
		this.title = title;
		this.introduce = introduce;
		this.endDate = endDate;
		this.maxMemberCount = maxMemberCount;
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
		if (this.hasJoinPasswd && !passwordEncoder.matches(joinPassword, this.joinPasswd)) {
			throw new NotMatchedPasswordException();
		}
	}

	private void checkMemberCount() {
		log.warn("current Member size : {}", this.groupMembers.size());
		if (this.groupMembers.size() == this.maxMemberCount) {
			throw new ExceedLimitMemberException();
		}
	}

	public void validateOwner(Long userId) {
		if (!Objects.equals(userId, this.ownerId)) {
			throw new BookGroupOwnerNotMatchedException();
		}
	}

	public void validateDelete(Long userId) {
		validateOwner(userId);
		if (this.groupMembers.size() > 1) {
			throw new CannotDeleteMemberExistException();
		}
	}

	public void checkCanLeave(Long userId) {
		if (Objects.equals(this.getOwnerId(), userId)) {
			throw new CannotLeaveGroupOwnerException();
		}
	}

}
