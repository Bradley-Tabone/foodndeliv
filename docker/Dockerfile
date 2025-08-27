FROM eclipse-temurin:17-jre

WORKDIR /app
COPY target/foodndeliv-0.0.1-SNAPSHOT.jar /app/foodndeliv.jar
COPY docker/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar
EXPOSE 8080

ENV JAVA_AGENT=""
ENV OTEL_TRACES_EXPORTER="jaeger"
ENV OTEL_EXPORTER_JAEGER_ENDPOINT="http://jaeger-all-in-one:14250"
ENV OTEL_SERVICE_NAME="foodndeliv"
ENV OTEL_TRACES_SAMPLER="always_on"

ENTRYPOINT ["sh", "-c", "java $JAVA_AGENT -jar foodndeliv.jar"]

#Simple build for your own architecture
#docker build -t markvellaum/foodndeliviamrsl:latest .
#docker push markvellaum/foodndeliviamrsl:latest

#Multiarch build

#docker buildx version
#docker buildx create --name multiarch-builder --use --driver docker-container
#docker buildx inspect --bootstrap
#docker buildx ls

#Note: unless containerd image store is disabled you MUST --push to a remote image registry
#docker buildx build --platform linux/amd64,linux/arm64  -t markvellaum/foodndeliviamrsl:latest -f Dockerfile .  --push

#Spring Boot Env variables and default values
#DB_HOST=localhost
#DB_PORT=5432
#DB_USER=myuser
#DB_PASSWORD
#$IAM_HOST=my-keycloak
#$IAM_PORT=80
#IAM_CLIENT_SECRET
#$IAM_PASSWORD
