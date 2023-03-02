package com.dadok.gaerval.global.config.security.filter;

import static org.apache.commons.lang3.CharEncoding.*;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.UnAuthenticationException;
import com.dadok.gaerval.global.error.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

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
		} catch (UnAuthenticationException e) {
			setErrorResponse(request, response, e);
		} catch (RuntimeException e) {
			setErrorResponse(request, response, e);
			log.warn("path : {}, Exception Name : {}, Message : {}",
				request.getRequestURI(), e.getClass().getSimpleName(), e.getMessage());
		}

	}

	private void setErrorResponse(HttpServletRequest request,
		HttpServletResponse response,
		RuntimeException e) throws IOException {
		HttpStatus status = HttpStatus.BAD_REQUEST;

		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding(UTF_8);

		ErrorResponse errorResponse = ErrorResponse.of(status, e.getMessage(), request.getRequestURI());

		response.getWriter()
			.print(objectMapper.writeValueAsString(
				new ResponseEntity<>(errorResponse, status)));

	}

	private void setErrorResponse(HttpServletRequest request,
		HttpServletResponse response,
		UnAuthenticationException e) throws IOException {
		ErrorCode errorCode = e.getErrorCode();
		HttpStatus status = errorCode.getStatus();

		response.setStatus(status.value());
		response.setContentType("application/json");
		response.setCharacterEncoding(UTF_8);

		ErrorResponse errorResponse = ErrorResponse.of(status, e.getMessage(), request.getRequestURI());

		response.getWriter().print(objectMapper.writeValueAsString(
				new ResponseEntity<>(errorResponse, status)));
	}

}

