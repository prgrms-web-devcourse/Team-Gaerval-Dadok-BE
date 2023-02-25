package com.dadok.gaerval.global.oauth;

import java.util.HashMap;
import java.util.Map;

import com.dadok.gaerval.global.config.security.AuthProvider;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * provider 마다 제공해주는 정보 값들이 다르기 때문에, 분기처리를 위해서 구현한 클래스
 */
@Builder(access = AccessLevel.PRIVATE)
@Getter
public class OAuth2Attribute {
	private Map<String, Object> attributes;
	private String attributeKey;
	private String email;
	private String name;
	private String picture;
	private AuthProvider authProvider;
	private String oauthId;

	public static OAuth2Attribute of(String provider, String attributeKey,
		Map<String, Object> attributes) {

		switch (provider) {
			case "kakao":
				return ofKakao("email", attributes);
			case "naver":
				return ofNaver("id", attributes);
			default:
				throw new RuntimeException();
		}
	}

	private static OAuth2Attribute ofKakao(String attributeKey,
		Map<String, Object> attributes) {
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

		//todo : 이메일 고려
		String email =
			kakaoAccount.get("email") == null ? null
				: (String) kakaoAccount.get("email");

		return OAuth2Attribute.builder()
			.name((String) kakaoProfile.get("nickname"))
			.email(email)
			.picture((String)kakaoProfile.get("profile_image_url"))
			.attributes(kakaoAccount)
			.attributeKey(attributeKey)
			.authProvider(AuthProvider.KAKAO)
			.oauthId(attributes.get("id").toString())
			.build();
	}

	private static OAuth2Attribute ofNaver(String attributeKey,
		Map<String, Object> attributes) {
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		return OAuth2Attribute.builder()
			.name((String) response.get("name"))
			.email((String) response.get("email"))
			.picture((String) response.get("profile_image"))
			.attributes(response)
			.attributeKey(attributeKey)
			.authProvider(AuthProvider.NAVER)
			.oauthId(response.get("id").toString())
			.build();
	}

	Map<String, Object> convertToMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("id", attributeKey);
		map.put("key", attributeKey);
		map.put("name", name);
		map.put("email", email);
		map.put("picture", picture);

		return map;
	}
}