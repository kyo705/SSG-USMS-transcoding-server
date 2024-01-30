FROM openjdk:11

RUN apt-get update && apt-get install -y ffmpeg

ARG JAR_FILE=./transcoding-0.0.1-SNAPSHOT.jar

COPY ${JAR_FILE} app.jar

ENTRYPOINT ["java","-jar","/app.jar"]
