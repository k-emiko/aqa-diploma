FROM openjdk:8-slim
WORKDIR ./app
COPY . .
CMD ["./wait-for-it.sh", "--timeout=30", "mysql:3306", "--", "java", "-jar", "aqa-shop.jar"]
EXPOSE 8080

