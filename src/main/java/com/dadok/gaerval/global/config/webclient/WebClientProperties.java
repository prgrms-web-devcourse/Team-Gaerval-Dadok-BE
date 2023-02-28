package com.dadok.gaerval.global.config.webclient;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "search-api-provider")
public class WebClientProperties {

	private String apiKey;
	private String scheme;
	private String baseUri;

	public WebClientProperties(String scheme, String baseUri, String apiKey) {
		this.scheme = scheme;
		this.baseUri = baseUri;
		this.apiKey = apiKey;
	}

}
