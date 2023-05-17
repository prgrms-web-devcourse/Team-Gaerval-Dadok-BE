package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
import com.dadok.gaerval.domain.book.repository.BookRecentSearchRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.common.logging.LogMethodInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SaveKeywordEventListener {

	private final UserService userService;
	private final BookRecentSearchRepository bookRecentSearchRepository;

	@EventListener
	@LogMethodInfo
	public void handleSaveKeywordEvent(SaveKeywordEvent event) {
		Optional<User> user = userService.findById(event.getUserId() == null ? 0L : event.getUserId());
		user.ifPresent(value -> {
			BookRecentSearch bookRecentSearch = BookRecentSearch.create(value, event.getKeyword());
			if (bookRecentSearchRepository.existsByKeywordAndUserId(event.getKeyword(), event.getUserId())) {
				bookRecentSearchRepository.updateRecentSearchKeyword(bookRecentSearch);
			} else {
				bookRecentSearchRepository.save(bookRecentSearch);
			}
		});
		log.info("User[{}]-Keyword[{}] EventListener done", event.getUserId(), event.getKeyword());
	}
}
