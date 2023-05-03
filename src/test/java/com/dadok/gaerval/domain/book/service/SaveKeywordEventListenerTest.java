package com.dadok.gaerval.domain.book.service;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.book.entity.BookRecentSearch;
import com.dadok.gaerval.domain.book.repository.BookRecentSearchRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class SaveKeywordEventListenerTest {

	@InjectMocks
	private SaveKeywordEventListener saveKeywordEventListener;

	@Mock
	private UserService userService;

	@Mock
	private BookRecentSearchRepository bookRecentSearchRepository;

	@DisplayName("handleSaveKeywordEvent - 사용자가 존재하면 최금 검색어를 저장하거나 업데이트한다. ")
	@Test
	void handleSaveKeywordEvent() {
		// given
		Long userId = 1L;
		String keyword = "test keyword";
		SaveKeywordEvent saveKeywordEvent = new SaveKeywordEvent(userId, keyword);

		User user = UserObjectProvider.createKakaoUser();
		ReflectionTestUtils.setField(user, "id", userId);
		BookRecentSearch bookRecentSearch = BookRecentSearch.create(user, keyword);

		when(userService.findById(eq(userId))).thenReturn(Optional.of(user));
		when(bookRecentSearchRepository.existsByKeywordAndUserId(eq(keyword), eq(userId))).thenReturn(true);

		// when
		saveKeywordEventListener.handleSaveKeywordEvent(saveKeywordEvent);

		// then
		verify(userService).findById(userId);
		verify(bookRecentSearchRepository).existsByKeywordAndUserId(keyword, userId);
	}

	@DisplayName("handleSaveKeywordEvent - 사용자가 존재하면 최금 검색어를 저장하거나 업데이트하는데 실패한다.")
	@Test
	void handleSaveKeywordEventWithAbsentUser() {
		// given
		Long userId = 1L;
		String keyword = "test keyword";
		SaveKeywordEvent saveKeywordEvent = new SaveKeywordEvent(userId, keyword);

		when(userService.findById(eq(userId))).thenReturn(Optional.empty());

		// when
		saveKeywordEventListener.handleSaveKeywordEvent(saveKeywordEvent);

		// then
		verify(userService).findById(userId);
		verify(bookRecentSearchRepository, never()).existsByKeywordAndUserId(any(), any());
		verify(bookRecentSearchRepository, never()).updateRecentSearchKeyword(any());
		verify(bookRecentSearchRepository, never()).save(any());
	}
}
