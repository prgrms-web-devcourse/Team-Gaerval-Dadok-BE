package com.dadok.gaerval.domain.book.dto.response;

import java.util.List;

public record BookResponses (
	Boolean isEnd,
	Integer pageableCount,
	Integer totalCount,
	List<SearchBookResponse> searchBookResponseList){
}
