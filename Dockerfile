FROM openjdk:8-alpine
MAINTAINER u6k.apps@gmail.com

# Setup application
RUN mkdir -p /opt/
COPY target/narou-analyze.jar /opt/

# Setup docker run setting
EXPOSE 8080

CMD ["java", "-jar", "/opt/narou-analyze.jar"]
