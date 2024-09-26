FROM maven:3-openjdk-17 as builder

WORKDIR /tmp/app

COPY target/blog-0.0.1.jar blog-0.0.1.jar

COPY .env .env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "blog-0.0.1.jar"]
