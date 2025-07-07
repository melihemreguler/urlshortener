# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Install Maven
RUN apk add --no-cache maven

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage - Multi-platform Alpine
FROM alpine:3.19
WORKDIR /app

# Install OpenJDK 17 JRE and curl for New Relic agent download
RUN apk add --no-cache openjdk17-jre curl

# Download New Relic Java agent
RUN curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip && \
    unzip newrelic-java.zip && \
    rm newrelic-java.zip

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
CMD ["java", "-javaagent:/app/newrelic/newrelic.jar", "-jar", "app.jar"]

