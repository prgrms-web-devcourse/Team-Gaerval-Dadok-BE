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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.DuplicateException;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.error.response.ErrorResponse;
import com.dadok.gaerval.global.error.response.ErrorResponse.FieldError;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolationException(
		DataIntegrityViolationException e, HttpServletRequest request
	) {
		//todo: slack
		logWarn(e, request.getRequestURI());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(value = DuplicateException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateExceptionException(
		DuplicateException e, HttpServletRequest request) {

		ErrorCode errorCode = e.getErrorCode();
		//todo: slack
		logWarn(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
		MethodArgumentTypeMismatchException e, HttpServletRequest request) {

		logInfo(e, request.getRequestURI());

		ErrorResponse errorResponse = ErrorResponse.badRequest(e.getParameter().getParameterName(),
			request.getRequestURI(),
			List.of(new FieldError(e.getName(), Objects.requireNonNull(e.getValue()).toString(), e.getMessage())));

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(value = NullPointerException.class)
	public ResponseEntity<ErrorResponse> handleNullPointException(
		NullPointerException e, HttpServletRequest request) {

		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
		HttpServletRequest request, ConstraintViolationException e) {

		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()
				, makeFieldErrorsFromConstraintViolations(e.getConstraintViolations())));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(
		HttpServletRequest request, RuntimeException e) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
		HttpServletRequest request, IllegalArgumentException e) {
		logInfo(e, request.getRequestURI());

		return badRequest(e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(InvalidArgumentException.class)
	public ResponseEntity<ErrorResponse> handleInvalidArgumentException(
		HttpServletRequest request, InvalidArgumentException e) {

		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest().body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
		HttpServletRequest request, MethodArgumentNotValidException e) {

		logInfo(e, request.getRequestURI());


		return ResponseEntity.badRequest().body(ErrorResponse.badRequest(
			e.getMessage(), request.getRequestURI(),
			makeFieldErrorsFromBindingResult(e.getBindingResult())
		));
	}

	@ExceptionHandler(InvalidFormatException.class)
	public ResponseEntity<ErrorResponse> handleInvalidFormatException(
		HttpServletRequest request, InvalidFormatException e) {
		logInfo(e, request.getRequestURI());

		return badRequest(e.getMessage(), request.getRequestURI());
	}

	@ExceptionHandler(AlreadyContainBookshelfItemException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyContainBookshelfItemException(
		HttpServletRequest request, AlreadyContainBookshelfItemException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(BookshelfUserNotMatchedException.class)
	public ResponseEntity<ErrorResponse> handleBookshelfUserNotMatchedException(
		HttpServletRequest request, BookshelfUserNotMatchedException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
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

	private static ResponseEntity<ErrorResponse> badRequest(String message, String path) {
		return ResponseEntity.badRequest().body(ErrorResponse.of(HttpStatus.BAD_REQUEST, message, path));
	}

	private static ResponseEntity<ErrorResponse> of(ErrorCode errorCode, String path) {

		ErrorResponse errorResponse = ErrorResponse.of(errorCode, path);

		return switch (errorCode.getStatus()) {
			case NOT_FOUND -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
			case UNAUTHORIZED -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
			case INTERNAL_SERVER_ERROR -> ResponseEntity.internalServerError().body(errorResponse);
			case BAD_REQUEST -> ResponseEntity.badRequest().body(errorResponse);
			default -> ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
		};
	}

	private void logWarn(Exception e, String path) {
		log.warn("path : {}, Exception Name : {}, Message : {}", path, e.getClass().getSimpleName(), e.getMessage());
	}

	private void logInfo(Exception e, String path) {
		log.info("path : {}, Exception Name : {}, Message : {}", path, e.getClass().getSimpleName(), e.getMessage());

		if (e instanceof BindException be) {
			log.info("path : {}, Exception Name : {}, Message : {}, params : {}", path, be.getClass().getSimpleName(),
				e.getMessage(), makeFieldErrorsFromBindingResult(be.getBindingResult()));

		} else if (e instanceof ConstraintViolationException ve) {
			log.info("path : {}, Exception Name : {}, Message : {}, params : {}", path, ve.getClass().getSimpleName(),
				e.getMessage(), makeFieldErrorsFromConstraintViolations(ve.getConstraintViolations()));
		} else {
			log.info("path : {}, Exception Name : {}, Message : {}", path, e.getClass().getSimpleName(),
				e.getMessage());
		}

	}

}
