package com.dadok.gaerval.testutil;

import java.util.HashMap;
import java.util.Map;

import com.dadok.gaerval.domain.job.entity.Job;
import com.dadok.gaerval.domain.user.entity.Role;
import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.entity.UserAuthority;
import com.dadok.gaerval.global.config.security.AuthProvider;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

public class UserObjectProvider {

	public static final String KAKAO_ATTRIBUTE_KEY = "id";
	public static final String NAVER_ATTRIBUTE_KEY = "response";


	public static final String USERNAME = "김다독";
	public static final String NICKNAME = "책장왕다독이";
	public static final String KAKAO_EMAIL = "dadok@kakao.com";
	public static final String NAVER_EMAIL = "dadok@naver.com";
	public static final String oauthId = "abcdfgh123";
	public static final String PICTURE_URL = "https://image.dadokcdn.dadok.io/abcdfgpe.jpg";

	public static Map<String, Object> attributes(AuthProvider authProvider) {
		Map<String, Object> attributes = new HashMap<>();

		switch (authProvider) {
			case KAKAO -> {
				Map<String, Object> kakaoAccount = new HashMap<>();
				Map<String, Object> kakaoProfile = new HashMap<>();

				kakaoAccount.put("email", KAKAO_EMAIL);

				kakaoProfile.put("nickname", NICKNAME);
				kakaoProfile.put("profile_image_url", PICTURE_URL);

				attributes.put("kakao_account", kakaoAccount);
				kakaoAccount.put("profile", kakaoProfile);
				attributes.put("id", oauthId);

				return attributes;
			}
			case NAVER -> {
				Map<String, Object> response = new HashMap<>();
				response.put("name", NICKNAME);
				response.put("email", NAVER_EMAIL);
				response.put("profile_image", PICTURE_URL);
				response.put("id", oauthId);
				attributes.put("response", response);
				return attributes;
			}
		}

		return attributes;
	}

	public static User createKakaoUser() {
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = attributes(AuthProvider.KAKAO);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, KAKAO_ATTRIBUTE_KEY, attributes);
		User user = User.createByOAuth(oAuth2Attribute, userAuthority);

		return user;
	}

	public static User createNaverUser() {
		UserAuthority userAuthority = UserAuthority.create(Role.USER);
		Map<String, Object> attributes = attributes(AuthProvider.NAVER);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.NAVER, NAVER_ATTRIBUTE_KEY, attributes);
		User user = User.createByOAuth(oAuth2Attribute, userAuthority);

		return user;
	}

	public static OAuth2Attribute naverAttribute() {
		Map<String, Object> attributes = attributes(AuthProvider.NAVER);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.NAVER, NAVER_ATTRIBUTE_KEY, attributes);
		return oAuth2Attribute;
	}

	public static OAuth2Attribute kakaoAttribute() {
		Map<String, Object> attributes = attributes(AuthProvider.KAKAO);
		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(AuthProvider.KAKAO, KAKAO_ATTRIBUTE_KEY, attributes);
		return oAuth2Attribute;
	}

	public static User createKakaoUser(Job job) {
		User kakaoUser = createKakaoUser();
		kakaoUser.changeJob(job);
		return kakaoUser;
	}

}
