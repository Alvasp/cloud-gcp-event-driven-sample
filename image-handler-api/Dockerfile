# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build

# Set the working directory
WORKDIR /app

#Set env variables
ENV PORT 8080
ENV APP_BUCKET_ENDPOINT http://localhost:9000
ENV APP_BUCKET_KEY youraccesskey
ENV APP_BUCKET_SECRET yoursecretkey
ENV APP_BUCKET_NAME bucketname
ENV APP_BUCKET_INPUT input/
ENV APP_BUCKET_OUTPUT output/
ENV APP_PUBSUB_ERRORS_SUBSCRIPTION errors_sub_name
ENV APP_PUBSUB_REQUEST_TOPIC request_topic
ENV APP_PUBSUB_RESPONSE_SUBSCRIPTION response_sub_name

# Copy the Maven pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:24-slim

# Set the working directory
WORKDIR /app

# Copy the JAR file from the build stage
COPY --from=build /app/target/*.jar /app/app.jar

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]