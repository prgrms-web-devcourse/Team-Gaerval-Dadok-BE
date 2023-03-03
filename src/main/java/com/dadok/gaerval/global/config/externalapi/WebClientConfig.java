package com.dadok.gaerval.global.config.externalapi;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

	private final WebClientProperties webClientProperties;

	@Bean
	public ReactorResourceFactory resourceFactory() {
		ReactorResourceFactory factory = new ReactorResourceFactory();
		factory.setUseGlobalResources(false);
		return factory;
	}

	@Bean
	public WebClient webClient(){
		return WebClient.builder()
			.baseUrl(webClientProperties.getBaseUri())
			.defaultHeader(HttpHeaders.AUTHORIZATION,
				String.format("%s %s", webClientProperties.getScheme(), webClientProperties.getApiKey()))
			.build();
	}


}
