package com.dadok.gaerval.global.config.externalapi;

import org.springframework.web.reactive.function.client.WebClient;

public interface VendorWebClient {
	WebClient getWebClient();
}
