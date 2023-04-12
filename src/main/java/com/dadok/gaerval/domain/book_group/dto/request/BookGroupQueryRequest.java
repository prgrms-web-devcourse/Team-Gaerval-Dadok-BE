package com.dadok.gaerval.domain.book_group.dto.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.dadok.gaerval.global.common.EnumType;
import com.dadok.gaerval.global.util.SortDirection;

import lombok.AllArgsConstructor;
import lombok.Getter;

public record BookGroupQueryRequest(
	@Min(value = 0, message = "pageSize는 최소 0 입니다.")
	@Max(value = 100, message = "pageSize는 최대 100 입니다.")
	Integer pageSize,

	Long groupCursorId,

	@NotNull
	BookGroupQueryRequest.GroupSearchOption option,

	@NotBlank(message = "검색어는 비어있으면 안됩니다.")
	@Min(value = 2, message = "검색어는 {min} 자 이상이여야 합니다.")
	String query,

	SortDirection sortDirection
) {

	public BookGroupQueryRequest(Integer pageSize,
		Long groupCursorId,
		GroupSearchOption option,
		String query,
		SortDirection sortDirection) {
		this.pageSize = (pageSize == null) ? 10 : pageSize;
		this.groupCursorId = groupCursorId;
		this.sortDirection = sortDirection == null ? SortDirection.DESC : sortDirection;

		this.option = option;
		this.query = query;
	}

	@Getter
	@AllArgsConstructor
	public enum GroupSearchOption implements EnumType {
		GROUP_NAME("GROUP_NAME", "그룹 이름"),
		BOOK_NAME("BOOK_NAME", "책 이름");

		private final String name;

		private final String description;

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getDescription() {
			return description;
		}
	}
}
