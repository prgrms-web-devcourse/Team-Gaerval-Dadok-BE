package com.dadok.gaerval.global.util;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.data.domain.Sort;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QueryDslUtil {

	public static <T> Slice<T> toSlice(List<T> contents, Pageable pageable) {

		boolean hasNext = isContentSizeGreaterThanPageSize(contents, pageable);
		return new SliceImpl<>(hasNext ? subListLastContent(contents, pageable) : contents, pageable, hasNext);
	}

	// 다음 페이지 있는지 확인
	private static <T> boolean isContentSizeGreaterThanPageSize(List<T> content, Pageable pageable) {
		return pageable.isPaged() && content.size() > pageable.getPageSize();
	}

	// 데이터 1개 빼고 반환
	private static <T> List<T> subListLastContent(List<T> content, Pageable pageable) {
		return content.subList(0, pageable.getPageSize());
	}

	public static BooleanExpression generateCursorWhereCondition(NumberPath<Long> cursorField, Long cursorId,
		Sort.Direction direction) {

		if (direction == null) {
			direction = Sort.Direction.DESC;
		}

		if (cursorId == null) {
			return null;
		}

		if (direction.isDescending()) {
			return cursorField.lt(cursorId);
		}

		return cursorField.gt(cursorId);
	}

	public static BooleanExpression generateIdWhereCondition(NumberPath<Long> entityId, Long id) {
		if (id == null) {
			return null;
		}
		return entityId.eq(id);
	}

	public static OrderSpecifier<?> getOrder(NumberPath<Long> id, Sort.Direction direction) {
		return direction.isDescending() ? id.desc() : id.asc();
	}

}
