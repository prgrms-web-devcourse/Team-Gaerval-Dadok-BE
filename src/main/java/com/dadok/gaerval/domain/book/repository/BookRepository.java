package com.dadok.gaerval.domain.book.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dadok.gaerval.domain.book.entity.Book;


@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
