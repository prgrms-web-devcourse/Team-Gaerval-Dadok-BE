package com.dadok.gaerval.global.config.web;

import static org.springframework.http.HttpMethod.*;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@EnableConfigurationProperties
@EnableWebMvc
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
			.allowedOriginPatterns("*","http://localhost:8080", "http://localhost:3000", "/**", "https://localhost:3000",
				"https://dadok.vercel.app", "http://127.0.0.1:3000"
				)
			.allowedMethods(GET.name(), POST.name(), PUT.name(), PATCH.name(), DELETE.name(), OPTIONS.name(),
				HEAD.name())
			.allowCredentials(true);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/docs/**").addResourceLocations("classpath:/static/docs/");
		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/templates/");
		registry.addResourceHandler("/favicon.ico")
			.addResourceLocations("classpath:/static/");
	}

	@Bean
	public ObjectMapper ObjectMapper() {
		return new ObjectMapper().registerModule(new JavaTimeModule());
	}

}
