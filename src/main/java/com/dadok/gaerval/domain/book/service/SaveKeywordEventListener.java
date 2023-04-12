package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
import com.dadok.gaerval.domain.book.repository.BookRecentSearchRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SaveKeywordEventListener {

	private final UserService userService;
	private final BookRecentSearchRepository bookRecentSearchRepository;

	@EventListener
	public void handleSaveKeywordEvent(SaveKeywordEvent event) {
		Optional<User> user = userService.findById(event.getUserId());
		user.ifPresent(value -> bookRecentSearchRepository.save(BookRecentSearch.create(value, event.getKeyword())));
	}
}
