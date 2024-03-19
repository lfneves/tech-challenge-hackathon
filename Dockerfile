FROM  eclipse-temurin:17-jdk-focal

WORKDIR /app

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} /app/app.jar
COPY docker-compose.yml /app/docker-compose.yml

EXPOSE 8097

ENTRYPOINT ["java","-jar","app.jar"]



