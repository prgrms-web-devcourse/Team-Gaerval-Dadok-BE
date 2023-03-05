package com.dadok.gaerval.domain.book_group.service;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupCreateRequest;
import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.user.entity.User;

public interface BookGroupService {

	BookGroupResponses findAllBookGroups(BookGroupSearchRequest request);

	Long createBookGroup(User user, BookGroupCreateRequest request);
}
