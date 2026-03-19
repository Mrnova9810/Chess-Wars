FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY chess-server-0.0.1-SNAPSHOT.jar app.jar

CMD ["sh", "-c", "java -jar app.jar --server.port=$PORT"]