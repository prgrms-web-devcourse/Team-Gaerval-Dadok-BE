package com.dadok.gaerval.controller.document.utils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.PayloadSubsectionExtractor;

public class CustomResponseFieldsSnippet extends AbstractFieldsSnippet {

	public CustomResponseFieldsSnippet(String type, PayloadSubsectionExtractor<?> subsectionExtractor,
		List<FieldDescriptor> descriptors, Map<String, Object> attributes,
		boolean ignoreUndocumentedFields) {
		super(type, descriptors, attributes, ignoreUndocumentedFields,
			subsectionExtractor);
	}

	public CustomResponseFieldsSnippet(String type, List<FieldDescriptor> descriptors,
		boolean ignoreUndocumentedFields) {
		super(type, descriptors, null, ignoreUndocumentedFields);
	}

	@Override
	protected MediaType getContentType(Operation operation) {
		return operation.getResponse().getHeaders().getContentType();
	}

	@Override
	protected byte[] getContent(Operation operation) throws IOException {
		return operation.getResponse().getContent();
	}
}