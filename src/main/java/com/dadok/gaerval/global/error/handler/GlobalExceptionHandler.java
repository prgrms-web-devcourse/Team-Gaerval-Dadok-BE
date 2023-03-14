package com.dadok.gaerval.global.error.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.dadok.gaerval.domain.auth.exception.RefreshTokenAuthenticationNotFoundException;
import com.dadok.gaerval.domain.book.exception.AlreadyContainBookCommentException;
import com.dadok.gaerval.domain.book.exception.BookApiNotAvailableException;
import com.dadok.gaerval.domain.book_group.exception.AlreadyContainBookGroupException;
import com.dadok.gaerval.domain.book_group.exception.BookGroupOwnerNotMatchedException;
import com.dadok.gaerval.domain.book_group.exception.CannotDeleteMemberExistException;
import com.dadok.gaerval.domain.book_group.exception.ExceedLimitMemberException;
import com.dadok.gaerval.domain.book_group.exception.ExpiredJoinGroupPeriodException;
import com.dadok.gaerval.domain.book_group.exception.NotMatchedPasswordException;
import com.dadok.gaerval.domain.bookshelf.exception.AlreadyContainBookshelfItemException;
import com.dadok.gaerval.domain.bookshelf.exception.BookshelfUserNotMatchedException;
import com.dadok.gaerval.global.config.security.exception.JwtAuthenticationException;
import com.dadok.gaerval.global.error.ErrorCode;
import com.dadok.gaerval.global.error.exception.DuplicateException;
import com.dadok.gaerval.global.error.exception.InvalidArgumentException;
import com.dadok.gaerval.global.error.exception.ResourceNotfoundException;
import com.dadok.gaerval.global.error.response.ErrorResponse;
import com.dadok.gaerval.global.error.response.ErrorResponse.FieldError;
import com.dadok.gaerval.infra.slack.SlackException;
import com.dadok.gaerval.infra.slack.SlackService;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Info : 에러는 아니지만 주시해야할 것
 * Warning : 예외상황이긴 했지만 에러는 아닌 것
 * Error : 에러가 맞고 대응해야할 것
 * Fatal : 치명적인 것
 */
@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final SlackService slackService;

	@ExceptionHandler(JwtAuthenticationException.class)
	public ResponseEntity<ErrorResponse> handleJwtAuthenticationException(
		JwtAuthenticationException e,
		HttpServletRequest request
	) {
		ErrorCode errorCode = e.getErrorCode();

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(RefreshTokenAuthenticationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleRefreshTokenAuthenticationNotFoundException(
		RefreshTokenAuthenticationNotFoundException e,
		HttpServletRequest request
	) {
		ErrorCode errorCode = e.getErrorCode();

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleEntityNotFoundException(
		EntityNotFoundException e, HttpServletRequest request
	) {
		logError(e, request.getRequestURI());
		slackService.sendError(e, request.getRequestURI());

		return badRequest("관리자에게 문의하세요", request.getRequestURI());
	}

	@ExceptionHandler(LockTimeoutException.class)
	public ResponseEntity<ErrorResponse> handleLockTimeoutException(
		LockTimeoutException e, HttpServletRequest request
	) {
		logWarn(e, request.getRequestURI());
		slackService.sendWarn(e, request.getRequestURI());
		return badRequest(null, request.getRequestURI());
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<ErrorResponse> handlePersistenceException(
		PersistenceException e, HttpServletRequest request
	) {
		logWarn(e, request.getRequestURI());
		slackService.sendWarn(e, request.getRequestURI());

		return badRequest(null, request.getRequestURI());
	}

	@ExceptionHandler(NotMatchedPasswordException.class)
	public ResponseEntity<ErrorResponse> handleNotMatchedPasswordException(
		NotMatchedPasswordException e, HttpServletRequest request) {
		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(ExpiredJoinGroupPeriodException.class)
	public ResponseEntity<ErrorResponse> handleExpiredJoinGroupPeriodException(
		ExpiredJoinGroupPeriodException e, HttpServletRequest request) {
		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(value = ResourceNotfoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotfoundException(
		ResourceNotfoundException e, HttpServletRequest request) {

		logInfo(e, request.getRequestURI());

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
			.body(ErrorResponse.of(e.getErrorCode(), e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(value = SlackException.class)
	public ResponseEntity<Void> handleSlackException(
		SlackException e, HttpServletRequest request
	) {
		logError(e, request.getRequestURI());
		slackService.sendError(e, request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(value = DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolationException(
		DataIntegrityViolationException e, HttpServletRequest request
	) {
		logError(e, request.getRequestURI());
		slackService.sendError(e, request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	@ExceptionHandler(value = DuplicateException.class)
	public ResponseEntity<ErrorResponse> handleDuplicateExceptionException(
		DuplicateException e, HttpServletRequest request) {

		ErrorCode errorCode = e.getErrorCode();
		logError(e, request.getRequestURI());
		slackService.sendError(e, request.getRequestURI());

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
		slackService.sendInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolationException(
		HttpServletRequest request, ConstraintViolationException e) {

		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI(), ErrorCode.INVALID_ARGUMENT
				, makeFieldErrorsFromConstraintViolations(e.getConstraintViolations())));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(
		HttpServletRequest request, RuntimeException e) {
		logWarn(e, request.getRequestURI());

		slackService.sendWarn(e, request.getRequestURI());

		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(e.getMessage(), request.getRequestURI()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(
		HttpServletRequest request, AccessDeniedException e) {
		logInfo(e, request.getRequestURI());

		return of(ErrorCode.ACCESS_DENIED, request.getRequestURI());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleException(
		HttpServletRequest request, Exception e) {

		logError(e, request.getRequestURI());

		slackService.sendError(e, request.getRequestURI());

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

	@NotNull
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(@NotNull MethodArgumentNotValidException e,
		@NotNull HttpHeaders headers, @NotNull HttpStatus status, @NotNull WebRequest webRequest) {

		ServletWebRequest request = (ServletWebRequest)webRequest;

		logInfo(e, request.getRequest().getRequestURI());
		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(
				makeErrorMessageToMessage(e.getBindingResult()),
				request.getRequest().getRequestURI(),
				ErrorCode.INVALID_ARGUMENT,
				makeFieldErrorsFromBindingResult(e.getBindingResult())
			));
	}

	@NotNull
	@Override
	protected ResponseEntity<Object> handleBindException(@NotNull BindException e,
		@NotNull HttpHeaders headers,
		@NotNull HttpStatus status,
		@NotNull WebRequest webRequest) {
		ServletWebRequest request = (ServletWebRequest)webRequest;

		logInfo(e, request.getRequest().getRequestURI());
		return ResponseEntity.badRequest()
			.body(ErrorResponse.badRequest(
				makeErrorMessageToMessage(e.getBindingResult()),
				request.getRequest().getRequestURI(),
				ErrorCode.INVALID_ARGUMENT,
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

	@ExceptionHandler(AlreadyContainBookGroupException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyContainBookGroupException(
		HttpServletRequest request, AlreadyContainBookGroupException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(ExceedLimitMemberException.class)
	public ResponseEntity<ErrorResponse> handleExceedMaximumNumberOfMemberException(
		HttpServletRequest request, ExceedLimitMemberException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(BookGroupOwnerNotMatchedException.class)
	public ResponseEntity<ErrorResponse> handleBookGroupOwnerNotMatchedException(
		HttpServletRequest request, BookGroupOwnerNotMatchedException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(CannotDeleteMemberExistException.class)
	public ResponseEntity<ErrorResponse> handleCannotDeleteMemberExistException(
		HttpServletRequest request, CannotDeleteMemberExistException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(AlreadyContainBookCommentException.class)
	public ResponseEntity<ErrorResponse> handleAlreadyContainBookCommentException(
		HttpServletRequest request, AlreadyContainBookCommentException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	@ExceptionHandler(BookApiNotAvailableException.class)
	public ResponseEntity<ErrorResponse> handleBookApiNotAvailableException(
		HttpServletRequest request, BookApiNotAvailableException e) {

		ErrorCode errorCode = e.getErrorCode();
		logInfo(e, request.getRequestURI());

		return of(errorCode, request.getRequestURI());
	}

	private List<FieldError> makeFieldErrorsFromBindingResult(BindingResult bindingResult) {
		List<FieldError> fieldErrors = new ArrayList<>();

		for (org.springframework.validation.FieldError fieldError : bindingResult.getFieldErrors()) {
			FieldError error = FieldError.of(fieldError.getField(), fieldError.getRejectedValue() == null ? "null" :
				fieldError.getRejectedValue(), fieldError.getDefaultMessage());
			fieldErrors.add(error);
		}

		return fieldErrors;
	}

	private String makeErrorMessageToMessage(BindingResult bindingResult) {

		return bindingResult.getFieldErrors()
			.stream()
			.map(fieldError -> fieldError.getField()
				+ "의 파라미터가 잘못되었습니다. input value : "
				+ fieldError.getRejectedValue())
			.collect(Collectors.joining(" ,  "));
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
		return ResponseEntity.badRequest()
			.body(ErrorResponse.of(HttpStatus.BAD_REQUEST, message, path));
	}

	private static ResponseEntity<ErrorResponse> of(ErrorCode errorCode, String path) {

		ErrorResponse errorResponse = ErrorResponse.of(errorCode, path);

		return ResponseEntity.status(errorCode.getStatus()).body(errorResponse);
	}

	private void logWarn(Exception e, String path) {
		log.warn("path : {}, Exception Name : {}, Message : {}", path, e.getClass().getSimpleName(), e.getMessage());
	}

	private void logError(Exception e, String path) {
		log.error("path : {}, Exception Name : {}, Message : {}", path, e.getClass().getSimpleName(), e.getMessage());
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
