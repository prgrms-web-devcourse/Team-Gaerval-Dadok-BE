package com.dadok.gaerval.global.common;

/**
 * RestDocs를 이용하여 Enum 타입을 문서화 할 시,
 * 사용되는 타입에 enum이 없기 때문에 enum인 경우 따로 만들어야 합니다.
 * enum 타입에 EnumType 인터페이스를 상속받고,
 * getDescription()과 getName()을 오버라이딩 합니다.
 * <pre class="code>
 * public enum Gender implements EnumType {
 *     MALE("남자"),
 *     FEMALE("여자");
 *
 *     private final String description;
 *
 *	   @Override
 *     public String getDescription() {
 *         return this.description;
 *     }
 *
 *     @Override
 *     public String getName() {
 *         return this.name();
 *     }
 * }
 * </pre>
 *
 * @See 문서화 시킬 enum
 */
public interface EnumType {

	@JacocoExcludeGenerated
	String getName();

	@JacocoExcludeGenerated
	String getDescription();

}
