package com.dadok.gaerval.global.common.logging;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

	private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

	@Before("@annotation(com.dadok.gaerval.global.common.logging.LogHttpRequests) && execution(public * *(..)))")
	public void logHttpRequest(JoinPoint joinPoint) {
		String controllerClassName = joinPoint.getTarget().getClass().getSimpleName();
		String returnType = ((MethodSignature) joinPoint.getSignature()).getReturnType().getSimpleName();
		Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
		String methodName = method.getName();
		Parameter[] parameters = method.getParameters();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			Object value = joinPoint.getArgs()[i];
			sb.append(parameter.getName()).append("=").append(value).append("|");
		}

		log.info("[{}]-[{}]-[{}]-[{}]", controllerClassName, returnType, methodName, sb);
	}
}