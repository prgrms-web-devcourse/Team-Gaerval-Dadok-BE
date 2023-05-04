package com.dadok.gaerval.global.config.externalapi;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import lombok.Getter;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "search-api-provider")
public class WebClientProperties {

	private final Kakao kakao;
	private final Aladin aladin;

	public WebClientProperties(Kakao kakao, Aladin aladin) {
		this.kakao = kakao;
		this.aladin = aladin;
	}

	@Getter
	public static class Kakao {
		private final String apiKey;
		private final String scheme;
		private final String baseUri;

		public Kakao(String apiKey, String scheme, String baseUri) {
			this.apiKey = apiKey;
			this.scheme = scheme;
			this.baseUri = baseUri;
		}
	}

	@Getter
	public static class Aladin {
		private final String ttbKey;
		private final String bestSellerBaseUri;

		public Aladin(String ttbKey, String bestSellerBaseUri) {
			this.ttbKey = ttbKey;
			this.bestSellerBaseUri = bestSellerBaseUri;
		}
	}
}
