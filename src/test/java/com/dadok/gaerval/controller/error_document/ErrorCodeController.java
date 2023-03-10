package com.dadok.gaerval.controller.error_document;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.global.error.ErrorCode;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@RestController
public class ErrorCodeController {

	@GetMapping(value = "/error-code", consumes = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, ErrorCodeMap> findEnums() {

		Map<String, ErrorCodeMap> map = new HashMap<>();

		for (ErrorCode errorCode : ErrorCode.values()) {
			map.put(errorCode.name(), new ErrorCodeMap(errorCode));
		}

		return map;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@NoArgsConstructor
	@AllArgsConstructor
	@Getter
	public static class ErrorCodeResponse {
		private Map<ErrorCode, ErrorCodeMap> data;

	}

	// public record ErrorCodeResponse(
	// 	Map<ErrorCode, ErrorCodeMap> data
	// ) {
	//
	// 	public static ErrorCodeResponse of(Map<ErrorCode, ErrorCodeMap> data) {
	// 		return new ErrorCodeResponse(data);
	// 	}
	//
	// }

	@Getter
	@NoArgsConstructor
	protected static class ErrorCodeMap {

		private String code;

		private String message;

		private int status;

		public ErrorCodeMap(ErrorCode errorCode) {
			this.code = errorCode.getCode();
			this.message = errorCode.getMessage();
			this.status = errorCode.getStatus().value();
		}
	}

}
