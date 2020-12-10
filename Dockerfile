FROM openjdk:8-alpine

COPY target/uberjar/covid-tracker-clj.jar /covid-tracker-clj/app.jar

EXPOSE 3000

CMD ["java", "-jar", "/covid-tracker-clj/app.jar"]
