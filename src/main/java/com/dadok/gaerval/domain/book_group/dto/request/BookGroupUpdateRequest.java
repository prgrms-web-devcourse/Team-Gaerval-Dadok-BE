package com.dadok.gaerval.domain.book_group.dto.request;

import java.time.LocalDate;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

public record BookGroupUpdateRequest(
	@NotBlank(message = "title 입력되지않았습니다.")
	@Length(max = 30, message = "title은 최대 30자까지 입력 가능합니다.")
	String title,

	@NotBlank(message = "introduce 입력되지않았습니다.")
	@Length(max = 1000, message = "introduce 최대 1000자까지만 입력가능합니다.")
	String introduce,

	@FutureOrPresent(message = "endDate 은 null 혹은 과거 일수 없습니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
	LocalDate endDate,

	@Min(value = 1, message = "maxMemberCount는 자신 포함 1명 이상이여야합니다.")
	Integer maxMemberCount
) {
}
