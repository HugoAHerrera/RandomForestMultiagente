FROM openjdk:17-jdk-slim

WORKDIR /app

COPY lib ./lib
COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven

RUN mvn install:install-file -Dfile=lib/jade.jar -DgroupId=jade -DartifactId=jade -Dversion=4 -Dpackaging=jar

RUN mvn clean package -DskipTests

CMD ["java", "-jar", "target/randomforest-1.0-SNAPSHOT.jar"]