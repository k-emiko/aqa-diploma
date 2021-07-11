FROM openjdk:8-slim
WORKDIR ./app
COPY . .
CMD ["./wait-for-it.sh", "--timeout=30", "localhost:5432", "--", "java", "-jar", "aqa-shop.jar"]
EXPOSE 8080

