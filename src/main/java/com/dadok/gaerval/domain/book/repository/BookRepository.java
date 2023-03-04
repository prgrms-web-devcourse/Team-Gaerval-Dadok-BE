package com.dadok.gaerval.domain.book.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.dadok.gaerval.domain.book.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, BookSupport {
	Optional<Book> findBookByIsbn(@Param("isbn") String isbn);
}
