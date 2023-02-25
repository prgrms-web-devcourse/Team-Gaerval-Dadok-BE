package com.dadok.gaerval.global.config.security;

import java.util.Optional;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.dadok.gaerval.domain.user.entity.User;
import com.dadok.gaerval.domain.user.service.UserService;
import com.dadok.gaerval.global.oauth.OAuth2Attribute;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserService userService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		String registrationId = userRequest.getClientRegistration().getRegistrationId();

		OAuth2User oAuth2User = of(userRequest);

		String userNameAttributeName = userRequest
			.getClientRegistration()
			.getProviderDetails()
			.getUserInfoEndpoint().getUserNameAttributeName();

		OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(registrationId,
			userNameAttributeName,
			oAuth2User.getAttributes());

		User user = findOrRegister(oAuth2Attribute);

		return UserPrincipal.of(user, oAuth2User.getAttributes());
	}

	private OAuth2User of(OAuth2UserRequest userRequest) {
		return new DefaultOAuth2UserService().loadUser(userRequest);
	}

	private User findOrRegister(OAuth2Attribute attribute) {
		//todo : SuccessHandler로 옮길지 고민.
		Optional<User> userOptional = userService.findByEmailWithAuthorities(attribute.getEmail());

		if (userOptional.isEmpty()) {
			return userService.register(attribute);
		}

		return userOptional.get();
	}

}
