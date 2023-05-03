package com.dadok.gaerval.global.config.externalapi;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class AladinWebClientConfig implements VendorWebClient {

	private final WebClientProperties webClientProperties;

	@Bean
	@Qualifier("aladinWebClient")
	public WebClient aladinWebClient() {
		return WebClient.builder()
			.baseUrl(webClientProperties.getBestSellerBaseUri())
			.build();
	}

	@Override
	public WebClient getWebClient() {
		return aladinWebClient();
	}

	public MultiValueMap<String, String> defaultQueryParams() {
		MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
		queryParams.add("ttbkey", webClientProperties.getTtbKey());
		queryParams.add("SearchTarget", "Book");
		queryParams.add("output", "js");
		queryParams.add("Version", "20131101");
		return queryParams;
	}
}
