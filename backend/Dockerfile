# Resolve Maven dependencies
FROM maven:3.8.5-openjdk-17-slim AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve

# Run the application
FROM maven:3.8.5-openjdk-17-slim
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY . .
CMD ["mvn", "spring-boot:run"]

