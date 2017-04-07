FROM openjdk:8-alpine
MAINTAINER u6k.apps@gmail.com

# Setup application
COPY target/narou-crawler.jar /opt/

# Setup docker run setting
EXPOSE 8080

CMD ["java", "-jar", "/opt/narou-crawler.jar"]
