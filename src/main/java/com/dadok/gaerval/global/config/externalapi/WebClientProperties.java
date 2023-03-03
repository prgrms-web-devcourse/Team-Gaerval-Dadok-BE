package com.dadok.gaerval.global.config.externalapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "search-api-provider")
public class WebClientProperties {

	private final String apiKey;
	private final String scheme;
	private final String baseUri;

	public WebClientProperties(String scheme, String baseUri, String apiKey) {
		this.scheme = scheme;
		this.baseUri = baseUri;
		this.apiKey = apiKey;
	}

}
