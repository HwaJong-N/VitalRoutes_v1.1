FROM openjdk:17

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar
COPY src/main/resources/firebaseKey.json firebaseKey.json

ENTRYPOINT ["java","-jar","/app.jar"]