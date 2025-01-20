###### Stage 1: Build the application ######
FROM maven:3.9.9-eclipse-temurin-21 AS app-builder

WORKDIR /app

# copy source file into the stage
COPY pom.xml .
COPY src ./src

# package into JAR file
RUN mvn clean package -DskipTests



###### Stage 2: build optimized jre runtime ######
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

WORKDIR /app

COPY --from=app-builder /app/target/*.jar app.jar

# unpack the executable jar
RUN java -Djarmode=tools -jar app.jar extract

# get the jar deps
RUN jdeps --multi-release 21 \
    --class-path 'app/lib/*' \
    --ignore-missing-deps \
    --list-deps \
    app/app.jar > deps


# deps file transformation
RUN sed 's/^ *//' deps | tr '\n' ',' > deps



# build optimized jre runtime
RUN jlink --compress=zip-9 \
    --add-modules $(grep 'java[.[a-z]*]*' -o deps | tr '\n' ',') \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /optimized-jdk-21



###### Stage 3: Create a minimal runtime image ######
FROM alpine:latest

# define JAVA_HOME env variable
ENV JAVA_HOME=/opt/jdk/jdk-21

# Add app user
ARG APPLICATION_USER=spring

RUN apk add --no-cache shadow && \
    useradd -m -u 1001 $APPLICATION_USER



WORKDIR /app

# copy the jar file from the jre-builder stage
COPY --from=jre-builder app/app.jar app.jar

# copy the jre runtime from the jre-builder stage
COPY --from=jre-builder  /optimized-jdk-21 ${JAVA_HOME}

ENV PATH=$PATH:${JAVA_HOME}/bin

USER ${APPLICATION_USER}

# Run the application
CMD ["java", "-jar", "app.jar"]