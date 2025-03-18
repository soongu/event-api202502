# base image로 jdk 17버전 지정
FROM amazoncorretto:17

# 작업 디렉토리 설정
WORKDIR /app

# 모든 소스코드 복사
COPY . .

# 애플리케이션 빌드
RUN ./gradlew clean build

# 커맨드 실행
CMD ["java","-jar", "-Dspring.profiles.active=default","build/libs/event-0.0.1-SNAPSHOT.jar"]
