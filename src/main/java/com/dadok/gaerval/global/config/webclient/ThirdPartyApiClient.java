package com.dadok.gaerval.global.config.webclient;

import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

public class ThirdPartyApiClient {

	private final WebClient webClient;

	private String restApiKey ="xxxxx";

	private String apiScheme = "KakaoAK ";
	private String baseUrl = "https://dapi.kakao.com/v3/search/book";

	public ThirdPartyApiClient() {
		this.webClient = WebClient.builder()
			.baseUrl(baseUrl)
			.defaultHeader(HttpHeaders.AUTHORIZATION, apiScheme + restApiKey)
			.build();
	}

	public Mono<String> searchBooks(String query, int page, int size, String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("query", query)
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.retrieve()
			.bodyToMono(String.class);
	}

	public Mono<String> searchBooksWithTargetRestriction(String query, SearchTarget searchTarget, int page, int size,
		String sort) {
		return webClient.get()
			.uri(uriBuilder -> uriBuilder
				.queryParam("title", query)
				.queryParam("target", searchTarget.getName())
				.queryParam("page", page)
				.queryParam("size", size)
				.queryParam("sort", sort)
				.build())
			.retrieve()
			.bodyToMono(String.class);
	}

}