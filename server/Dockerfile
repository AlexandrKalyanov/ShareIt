FROM amazoncorretto:11-alpine-jdk
COPY target/*.jar shareit-server.jar
#ENV TZ="Europe/Moscow"
ENTRYPOINT ["java","-jar","/shareit-server.jar"]
