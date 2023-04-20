package com.dadok.gaerval.domain.book.service;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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

	@TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
	@LogMethodInfo
	public void handleSaveKeywordEvent(SaveKeywordEvent event) {
		Optional<User> user = userService.findById(event.getUserId());
		user.ifPresent(value -> bookRecentSearchRepository.save(BookRecentSearch.create(value, event.getKeyword())));
		log.info("User[{}]-Keyword[{}] TransactionalEventListener done", event.getUserId(), event.getKeyword());
	}
}
