FROM openjdk:11

WORKDIR /usr/src/app

COPY src/main/java/helloworld.java /usr/src/app

RUN javac helloworld.java

CMD ["java", "helloworld"]