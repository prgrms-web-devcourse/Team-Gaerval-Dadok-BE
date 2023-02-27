package com.dadok.gaerval.controller.document;

import java.util.Map;

import lombok.Builder;

/**
 * Enum 타입을 문서화 할 시 사용할 Test 패키지에서 만들 컨트롤러에서 사용할 클래스 입니다.
 * 문서화하고자 하는 모든 enum값을 Map<String, String> 타입으로 명시합니다.
 * <pre class="code">
 * public class EnumDocs {
 *     // 문서화하고 싶은 모든 enum값을 명시
 *     Map<String,String> gender;
 *     Map<String,String> status;
 *     ...
 * }
 * </pre>
 *
 * @See Enum
 * @See EnumType
 */
@Builder
public record EnumDocs(
		Map<String, String> jobGroup,
		Map<String, String> jobName
	) {

}
