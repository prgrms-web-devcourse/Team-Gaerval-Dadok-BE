package com.dadok.gaerval.domain.book_group.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import com.dadok.gaerval.domain.book.dto.request.BookCreateRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

public record BookGroupCreateRequest(
	@NotNull(message = "book 은 null 일수 없습니다.")
	BookCreateRequest book,

	@NotBlank(message = "title 입력되지않았습니다.")
	@Length(max = 30, message = "title은 최대 30자까지 입력 가능합니다.")
	String title,

	@FutureOrPresent(message = "startDate 은 null 혹은 과거 일수 없습니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate startDate,

	@FutureOrPresent(message = "endDate 은 null 혹은 과거 일수 없습니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate endDate,

	@NotNull(message = "maxMemberCount 은 null 일수 없습니다.")
	@Min(value = 1, message = "maxMemberCount는 자신 포함 1명 이상이여야합니다.")
	Integer maxMemberCount,

	@NotBlank(message = "introduce 입력되지않았습니다.")
	@Length(max = 1000, message = "introduce 최대 1000자까지만 입력가능합니다.")
	String introduce,

	@NotNull(message = "hasJoinPasswd 은 null 일수 없습니다.")
	Boolean hasJoinPasswd,

	@Size(max = 30, message = "joinQuestion 은 최대 30자까지 입력가능합니다.")
	String joinQuestion,

	@Size(max = 10, message = "joinPasswd 은 최대 10자까지 입력가능합니다.")
	String joinPasswd,

	@NotNull(message = "isPublic 은 null 일수 없습니다.")
	Boolean isPublic
) {
}
