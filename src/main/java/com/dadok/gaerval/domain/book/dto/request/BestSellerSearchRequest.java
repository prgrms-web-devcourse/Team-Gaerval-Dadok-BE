package com.dadok.gaerval.domain.book.dto.request;

import org.hibernate.validator.constraints.Range;

public record BestSellerSearchRequest(
	@Range(min = 1, max = 50, message = "page는 1에서 50사이 값입니다.")
	Integer page,
	@Range(min = 1, max = 50, message = "pageSize는 1에서 50사이 값입니다.")
	Integer pageSize,
	Integer categoryId,
	BestSellerSearchRange bestSellerSearchRange
) {
	public BestSellerSearchRequest(Integer page, Integer pageSize, Integer categoryId, BestSellerSearchRange bestSellerSearchRange) {
		this.page = (page == null) ? 1 : page;
		this.pageSize = (pageSize == null) ? 10 : pageSize;
		this.categoryId = (categoryId == null) ? 0 : categoryId;
		this.bestSellerSearchRange = bestSellerSearchRange;
	}
}
