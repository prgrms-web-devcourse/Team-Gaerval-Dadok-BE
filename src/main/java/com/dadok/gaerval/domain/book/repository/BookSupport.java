package com.dadok.gaerval.domain.book.repository;

import com.dadok.gaerval.domain.book.dto.request.SuggestionsBookFindRequest;
import com.dadok.gaerval.domain.book.dto.response.SuggestionsBookFindResponses;

public interface BookSupport {

	SuggestionsBookFindResponses findSuggestionBooks(SuggestionsBookFindRequest request);

}
