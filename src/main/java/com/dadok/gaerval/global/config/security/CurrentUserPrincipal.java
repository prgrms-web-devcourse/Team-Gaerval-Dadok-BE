package com.dadok.gaerval.global.config.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * @See anonymousUser 경우 EmptyUserPrincipal 저장
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "#this == 'anonymousUser' ? new com.dadok.gaerval.global.config.security.EmptyUserPrincipal() : #this")
public @interface CurrentUserPrincipal {
}
