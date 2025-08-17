FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/foodndeliv-0.0.1-SNAPSHOT.jar /app/foodndeliv.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "foodndeliv.jar"]

#Simple build for your own architecture
#docker build -t markvellaum/foodndeliviamrsl:ddl .
#docker push markvellaum/foodndeliviamrsl:ddl

#Multiarch build

#docker buildx version
#docker buildx create --name multiarch-builder --use --driver docker-container
#docker buildx inspect --bootstrap
#docker buildx ls

#Note: unless containerd image store is disabled you MUST --push to a remote image registry
#docker buildx build --platform linux/amd64,linux/arm64  -t markvellaum/foodndeliviamrsl:ddl -f Dockerfile .  --push

#Spring Boot Env variables and default values
#DB_HOST=localhost
#DB_PORT=5432
#DB_USER=admin
#DB_PASSWORD
#$IAM_HOST=my-keycloak
#$IAM_PORT=80
#IAM_CLIENT_SECRET
#$IAM_PASSWORD
