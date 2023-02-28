package com.dadok.gaerval.domain.book.converter;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.dadok.gaerval.domain.book.dto.response.SearchBookResponse;
import com.dadok.gaerval.domain.book.entity.Book;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BookMapper {

	SearchBookResponse entityToSearchBookResponse(Book book);
}
