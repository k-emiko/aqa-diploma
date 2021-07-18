FROM openjdk:8-slim
WORKDIR ./app
COPY . .
CMD ["./wait-for-it.sh", "--timeout=90", "psql:5432", "--", "java", "-jar", "aqa-shop.jar"]
EXPOSE 8080

