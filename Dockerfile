FROM maven:3-openjdk-17 as builder

WORKDIR /tmp/app

ADD . .

RUN mvn package -Dmaven.test.skip

EXPOSE 8080

WORKDIR /tmp/app/target

ENTRYPOINT ["java", "-jar", "blog-0.0.1.jar"]
