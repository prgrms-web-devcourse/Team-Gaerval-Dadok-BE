package com.dadok.gaerval.controller.document;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dadok.gaerval.domain.job.entity.JobGroup;
import com.dadok.gaerval.domain.user.entity.Gender;
import com.dadok.gaerval.global.common.EnumType;

/**
 * 문서화 하고 싶은 enum을 toMap()을 통해 Map으로 변환합니다.
 * 다음 EnumResponse에 전부 담아서 반환합니다.
 */
@RestController
@RequestMapping("/test")
public class EnumDocsController {

	@GetMapping("/enums")
	public EnumResponse<EnumDocs> findEnums() {

		return EnumResponse.of(EnumDocs.builder()
				.jobGroup(toMap(JobGroup.values()))
				.jobName(toMap(JobGroup.JobName.values()))
				.gender(toMap(Gender.values()))
			.build()
		);
	}

	private Map<String, String> toMap(EnumType[] enumTypes) {
		return Arrays.stream(enumTypes)
			.collect(Collectors.toMap(EnumType::getName, EnumType::getDescription));
	}

}
