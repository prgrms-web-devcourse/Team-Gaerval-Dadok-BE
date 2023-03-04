package com.dadok.gaerval.domain.book_group.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.book_group.dto.request.BookGroupSearchRequest;
import com.dadok.gaerval.domain.book_group.dto.response.BookGroupResponses;
import com.dadok.gaerval.domain.book_group.repository.BookGroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultBookGroupService implements BookGroupService {

	private final BookGroupRepository bookGroupRepository;

	@Transactional(readOnly = true)
	@Override
	public BookGroupResponses findAllBookGroups(BookGroupSearchRequest request) {
		return bookGroupRepository.findAllBy(request);
	}

}
