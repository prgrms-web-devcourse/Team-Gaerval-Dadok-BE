package com.dadok.gaerval.domain.bookshelf.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfLike;
import com.dadok.gaerval.domain.bookshelf.exception.AlreadyExistsBookshelfLikeException;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfLikeRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.testutil.JobObjectProvider;
import com.dadok.gaerval.testutil.UserObjectProvider;

@ExtendWith(MockitoExtension.class)
class DefaultBookshelfLikeServiceTest {

	@InjectMocks
	private DefaultBookshelfLikeService bookshelfLikeService;

	@Mock
	private BookshelfLikeRepository bookshelfLikeRepository;

	@Mock
	private BookshelfService bookshelfService;

	@Mock
	private UserService userService;

	private final User user = UserObjectProvider.createKakaoUser(JobObjectProvider.backendJob());
	private final Bookshelf bookshelf = Bookshelf.create(UserObjectProvider.createNaverUser());

	@Test
	void createBookshelfLike_success() {
		// Given
		ReflectionTestUtils.setField(user, "id", 1L);
		given(userService.getById(1L))
			.willReturn(user);
		given(bookshelfService.getById(2L))
			.willReturn(bookshelf);
		given(bookshelfLikeRepository.existsLike(2L, 1L))
			.willReturn(Boolean.FALSE);

		// When
		assertDoesNotThrow(() -> {
			bookshelfLikeService.createBookshelfLike(1L, 2L);
		});

		// Then
		assertThat(bookshelf.getBookshelfLikes().size()).isEqualTo(1);
	}

	@Test
	void createBookshelfLike_alreadyExist_fail() {
		// Given
		ReflectionTestUtils.setField(user, "id", 1L);
		given(userService.getById(1L))
			.willReturn(user);
		given(bookshelfService.getById(2L))
			.willReturn(bookshelf);
		given(bookshelfLikeRepository.existsLike(2L, 1L))
			.willReturn(Boolean.TRUE);

		// When // Then
		assertThrows(AlreadyExistsBookshelfLikeException.class, () -> {
			bookshelfLikeService.createBookshelfLike(1L, 2L);
		});
	}

	@Test
	void deleteBookshelfLike_success() {
		// Given
		ReflectionTestUtils.setField(user, "id", 1L);
		BookshelfLike bookshelfLike = BookshelfLike.create(user, bookshelf);

		given(bookshelfLikeRepository.findByUserIdAndBookshelfId(1L, 2L))
			.willReturn(Optional.of(bookshelfLike));

		// When // Then
		assertDoesNotThrow(() -> {
			bookshelfLikeService.deleteBookshelfLike(1L, 2L);
		});
	}

	@Test
	void deleteBookshelfLike_notExist_fail() {
		// Given
		given(bookshelfLikeRepository.findByUserIdAndBookshelfId(1L, 2L))
			.willReturn(Optional.empty());

		// When // Then
		assertThrows(ResourceNotfoundException.class, () -> {
			bookshelfLikeService.deleteBookshelfLike(1L, 2L);
		});
	}

}