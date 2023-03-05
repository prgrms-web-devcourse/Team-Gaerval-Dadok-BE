package com.dadok.gaerval.domain.book_group.repository;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;

interface BookGroupSupport {

	BookGroupResponses findAllBy(BookGroupSearchRequest request);

	BookGroupResponses findAllByUser(BookGroupSearchRequest request, Long userId);
}
