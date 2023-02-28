FROM openjdk:17-alpine

RUN apk add --no-cache bash

ARG JAR_FILE=build/libs/*.jar

# .env 파일 로드
ARG ENV_FILE=./.env
ENV ENV_FILE=${ENV_FILE}
RUN $(cat $ENV_FILE | xargs)

COPY ${JAR_FILE} dadok.jar

ENTRYPOINT ["java", \
"-Dspring.profiles.active=${SPRING_ACTIVE_PROFILE}", \
"-jar", "/dadok.jar"]