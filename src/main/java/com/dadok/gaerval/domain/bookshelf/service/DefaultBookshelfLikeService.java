package com.dadok.gaerval.domain.bookshelf.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfLike;
import com.dadok.gaerval.domain.bookshelf.exception.AlreadyExistsBookshelfLikeException;
import com.dadok.gaerval.domain.bookshelf.repository.BookshelfLikeRepository;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultBookshelfLikeService implements BookshelfLikeService {

	private final BookshelfLikeRepository bookshelfLikeRepository;

	private final UserService userService;

	private final BookshelfService bookshelfService;

	@Override
	public void createBookshelfLike(Long userId, Long bookshelfId) {
		User user = userService.getById(userId);
		Bookshelf bookshelf = bookshelfService.getById(bookshelfId);
		if (bookshelfLikeRepository.existsLike(bookshelfId, userId)) {
			throw new AlreadyExistsBookshelfLikeException(bookshelf.getId());
		}
		try {
			bookshelfLikeRepository.save(BookshelfLike.create(user, bookshelf));
		} catch (DataIntegrityViolationException e) {
			throw new AlreadyExistsBookshelfLikeException(bookshelf.getId());
		}
	}

	@Override
	public void deleteBookshelfLike(Long userId, Long bookshelfId) {
		BookshelfLike bookshelfLike = bookshelfLikeRepository.findByUserIdAndBookshelfId(userId, bookshelfId)
			.orElseThrow(() -> new ResourceNotfoundException(BookshelfLike.class));
		try {
			bookshelfLikeRepository.deleteById(bookshelfLike.getId());
		} catch (ObjectOptimisticLockingFailureException e) {
			throw new ResourceNotfoundException(BookshelfLike.class);
		}
	}
}
