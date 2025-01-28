
###### Stage 1: build optimized jre runtime ######
FROM eclipse-temurin:21-jdk-alpine AS jre-builder

WORKDIR /app

COPY  target/*.jar app.jar

# unpack the executable jar
RUN java -Djarmode=tools -jar app.jar extract

# get the jar deps
RUN jdeps --multi-release 21 \
    --class-path 'app/lib/*' \
    --ignore-missing-deps \
    --list-deps \
    app/app.jar > jar_modules

RUN (java --list-modules | cut -d '@' -f1) > available_modules

RUN grep -F -f available_modules jar_modules -o > result

# build optimized jre runtime
RUN jlink --compress=zip-9 \
    --add-modules $(cat result | tr '\n' ',') \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /optimized-jre-21



###### Stage 2: Create a minimal runtime image ######
FROM alpine:latest

# define JAVA_HOME env variable
ENV JAVA_HOME=/opt/jdk/jdk-21

# Add app user
ARG APPLICATION_USER=spring

# RUN apk add --no-cache shadow && \
# useradd -m -u 1001 $APPLICATION_USER


WORKDIR /app

# copy the jar file from the jre-builder stage
COPY --from=jre-builder app/app.jar app.jar

# copy the jre runtime from the jre-builder stage
COPY --from=jre-builder  /optimized-jre-21 ${JAVA_HOME}

ENV PATH=$PATH:${JAVA_HOME}/bin

# USER ${APPLICATION_USER}

# Run the application
CMD ["java", "-jar", "app.jar"]