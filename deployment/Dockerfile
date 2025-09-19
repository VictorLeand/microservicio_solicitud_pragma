FROM eclipse-temurin:21-jre-alpine
WORKDIR /opt/app
VOLUME /tmp

# Usa wildcard…
COPY applications/app-service/build/libs/*.jar app.jar
# …o el nombre exacto:
# COPY applications/app-service/build/libs/Solicitudes.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -Djava.security.egd=file:/dev/./urandom"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
