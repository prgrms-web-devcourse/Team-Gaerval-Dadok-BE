package com.dadok.gaerval.domain.bookshelf.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dadok.gaerval.domain.book.entity.Book;
import com.dadok.gaerval.domain.bookshelf.entity.Bookshelf;
import com.dadok.gaerval.domain.bookshelf.entity.BookshelfItem;

public interface BookshelfItemRepository extends JpaRepository<BookshelfItem, Long>, BookShelfItemSupport {

	Optional<BookshelfItem> findByBookshelfAndBook(@Param("bookshelf") Bookshelf bookshelf, @Param("book") Book Book);

	@Query("SELECT bsi FROM BookshelfItem bsi WHERE bsi.bookshelf.id IN :bookshelfIds AND bsi.book.id = :bookId")
	List<BookshelfItem> findAllByBookShelfIdsAndBookId(@Param("bookshelfIds") List<Long> bookshelfIds, @Param("bookId") Long bookId);
}
