package com.dadok.gaerval.global.error.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.error.response.ErrorResponse;
import com.dadok.gaerval.global.error.response.ErrorResponse.FieldError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e, HttpServletRequest request) {

		ErrorResponse errorResponse = ErrorResponse.badRequest(e.getParameter().getParameterName(),
			request.getRequestURI(),
			List.of(new FieldError(e.getName(), Objects.requireNonNull(e.getValue()).toString(), e.getMessage())));

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(value = NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointException(
		NullPointerException e, HttpServletRequest request) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
		HttpServletRequest request, ConstraintViolationException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()
				, makeFieldErrorsFromConstraintViolations(e.getConstraintViolations())));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(
		HttpServletRequest request, RuntimeException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
		HttpServletRequest request, IllegalArgumentException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorResponse> handleInvalidArgumentException(
		HttpServletRequest request, InvalidArgumentException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		HttpServletRequest request, MethodArgumentNotValidException e) {

		return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
			e.getMessage(), request.getRequestURI(),
			makeFieldErrorsFromBindingResult(e.getBindingResult())
		));
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormatException(
		HttpServletRequest request, InvalidFormatException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(AlreadyContainBookshelfItemException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyContainBookshelfItemException(
		HttpServletRequest request, AlreadyContainBookshelfItemException e) {

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(BookshelfUserNotMatchedException.class)
	public ResponseEntity<ErrorResponse> handleBookshelfUserNotMatchedException(
		HttpServletRequest request, BookshelfUserNotMatchedException e) {

		return ResponseEntity.status(HttpStatus.FORBIDDEN)
			.body(ErrorResponse.forbidden(e.getMessage(), request.getRequestURI(), List.of()));
	}

	private List<FieldError> makeFieldErrorsFromBindingResult(BindingResult bindingResult) {
		List<FieldError> fieldErrors = new ArrayList<>();

		for (org.springframework.validation.FieldError fieldError : bindingResult.getFieldErrors()) {
			FieldError error = FieldError.of(fieldError.getField(), Objects.requireNonNull(
				fieldError.getRejectedValue()), fieldError.getDefaultMessage());
			fieldErrors.add(error);
		}

		return fieldErrors;
	}

	private List<FieldError> makeFieldErrorsFromConstraintViolations(
		Set<ConstraintViolation<?>> constraintViolations) {

		return constraintViolations.stream()
			.map(violation -> FieldError.of(getFieldFromPath(violation.getPropertyPath()),
				violation.getInvalidValue(), violation.getMessage()))
			.collect(Collectors.toList());
	}

	private String getFieldFromPath(Path fieldPath) {
		PathImpl pathImpl = (PathImpl)fieldPath;
		return pathImpl.getLeafNode().toString();
	}

}
