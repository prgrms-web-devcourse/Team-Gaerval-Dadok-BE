package com.dadok.gaerval.global.common.logging;

import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

	@Before("@annotation(com.dadok.gaerval.global.common.logging.LogHttpRequests) && execution(public * *(..)))")
	public void logHttpRequest(JoinPoint joinPoint) {
		String controllerClassName = joinPoint.getTarget().getClass().getSimpleName();
		MethodSignature methodSignature = (MethodSignature)joinPoint.getSignature();
		String returnType = methodSignature.getReturnType().getSimpleName();
		Type genericReturnType = methodSignature.getMethod().getGenericReturnType();
		if (genericReturnType instanceof ParameterizedType) {
			ParameterizedType parameterizedType;
			parameterizedType = (ParameterizedType)genericReturnType;
			Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
			if (actualTypeArguments.length > 0 && actualTypeArguments[0] instanceof Class) {
				returnType = ((Class)actualTypeArguments[0]).getSimpleName();
			}
		}
		String methodName = methodSignature.getName();
		Parameter[] parameters = methodSignature.getMethod().getParameters();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			String value = joinPoint.getArgs()[i].toString();
			String parameterName = parameter.getName();
			if (value.startsWith("com.dadok")) {
				value = value.substring(value.lastIndexOf('.') + 1);
			}
			sb.append(parameterName).append("=").append(value).append("|");
		}

		log.info("[{}]-[{}]-[{}]-[{}]", controllerClassName, returnType, methodName, sb);
	}
}