package com.dadok.gaerval.global.config.security.filter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dadok.gaerval.global.config.security.SecurityConfig;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ExceptionHandlingFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			filterChain.doFilter(request, response);
		} catch (ExpiredJwtException e) {
			SecurityConfig.setErrorResponse(request, response, e, objectMapper);
		}
		catch (UnAuthenticationException e) {
			log.info("""
					path : {},
					Exception Name : {}, 
					Message : {}, 
					Request Header : {}
					""",
				request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage(),
				getHeaderValues(request)
				);
			SecurityConfig.setErrorResponse(request, response, e, objectMapper);
		} catch (AccessDeniedException ex) {
			System.out.println();
		} catch (RuntimeException e) {
			SecurityConfig.setErrorResponse(request, response, e, objectMapper);
			log.info("""
					path : {},
					Exception Name : {},
					Message : {}, 
					Request Header : {}
					""",
				request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage(),
				getHeaderValues(request)
			);
		}

	}

	private String getHeaderValues(HttpServletRequest request) throws JsonProcessingException {
		Enumeration<String> headerNames = request.getHeaderNames();

		Iterator<String> stringIterator = headerNames.asIterator();
		Map<String, String> map = new HashMap<>();

		while (stringIterator.hasNext()) {
			String key = stringIterator.next();
			map.put(key, request.getHeader(key));
		}

		String headerToString = "";

		headerToString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

		return headerToString;
	}

}

