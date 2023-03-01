package com.dadok.gaerval.global.config.web;

import java.util.Arrays;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableConfigurationProperties
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOrigins("*")
			.allowedMethods(Arrays.toString(HttpMethod.values()))
			.allowedOriginPatterns("http://localhost:8080", "http://localhost:3000", "/**")
			.allowedMethods(Arrays.toString(HttpMethod.values()))
			.allowCredentials(true);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/docs/**").addResourceLocations("classpath:/static/docs/");
		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/templates/");
	}

}
