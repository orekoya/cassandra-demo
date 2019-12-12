FROM maven:3.6.0-jdk-13-alpine AS build  
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  
RUN mvn -f /usr/src/app/pom.xml clean package

FROM openjdk:8-jre-alpine  
COPY --from=build /usr/src/app/target/example-1.0-SNAPSHOT.jar /usr/app/example-1.0-SNAPSHOT.jar  
CMD ["java","-jar","/usr/app/example-1.0-SNAPSHOT.jar"]
