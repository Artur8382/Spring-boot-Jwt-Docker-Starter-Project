# Use JDK 25 as you requested
FROM eclipse-temurin:25-jdk

WORKDIR /app

# Copy all files from the 'real' folder into the container
COPY . .

# IMPORTANT: Fix Windows line endings and permissions for the Maven wrapper
RUN apt-get update && apt-get install -y dos2unix
RUN dos2unix mvnw
RUN chmod +x mvnw

# Build the app inside the container
RUN ./mvnw clean package -DskipTests

EXPOSE 8080

# Use a wildcard to find the jar so the version number doesn't break the build
ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]