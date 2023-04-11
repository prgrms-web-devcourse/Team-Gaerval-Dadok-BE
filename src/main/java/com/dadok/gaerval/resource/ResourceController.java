package com.dadok.gaerval.resource;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class ResourceController {

	private final ResourceLoader resourceLoader;

	@GetMapping(value = {"/openapi3.yaml", "/openapi3.yml"})
	public ResponseEntity<Resource> downloadOpenapi3(
		HttpServletRequest request
	) throws IOException {

		Resource resource = resourceLoader.getResource("classpath:static/openapi3/openapi3.yaml");

		File file = resource.getFile();

		return ResponseEntity.ok()
			.header(HttpHeaders.CONTENT_DISPOSITION, file.getName())
			.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(file.length()))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)	//바이너리 데이터로 받아오기 설정
			.body(resource);
	}
}
