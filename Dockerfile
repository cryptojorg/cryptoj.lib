FROM adoptopenjdk/maven-openjdk11:latest

WORKDIR /usr/src/app

CMD ["java", "-version"]