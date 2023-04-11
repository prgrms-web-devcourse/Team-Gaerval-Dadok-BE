package com.dadok.gaerval.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
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
	public ResponseEntity<Resource> downloadOpenapi3(HttpServletRequest request) throws IOException {
		Resource resource = resourceLoader.getResource("classpath:static/openapi3/openapi3.yaml");
		try (InputStream inputStream = resource.getInputStream()) {
			File tempFile = File.createTempFile("openapi3", ".yml");
			FileUtils.copyInputStreamToFile(inputStream, tempFile);
			return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFilename())
				.header(HttpHeaders.CONTENT_LENGTH, String.valueOf(tempFile.length()))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
				.body(new InputStreamResource(new FileInputStream(tempFile)));
		}
	}
}
