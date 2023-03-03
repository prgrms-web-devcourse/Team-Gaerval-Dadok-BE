package com.dadok.gaerval.global.config.externalapi;

import java.nio.charset.StandardCharsets;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.dadok.gaerval.domain.book.dto.request.SearchTarget;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class WebClientBookApiOperations implements ExternalBookApiOperations{

	private final WebClient webClient;

	@Override
	public Flux<String> searchBooks(String query, int page, int size, String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.acceptCharset(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(String.class);
	}

	@Override
	public Flux<String> searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("target", searchTarget.getName())
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.acceptCharset(StandardCharsets.UTF_8)
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToFlux(String.class);
	}
}
