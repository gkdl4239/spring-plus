# 1. 기본 이미지 설정 (OpenJDK 사용)
FROM openjdk:17-jdk-slim

# 2. 애플리케이션 jar 파일 복사
ARG JAR_FILE=build/libs/expert.jar
COPY ${JAR_FILE} app.jar

# 3. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]