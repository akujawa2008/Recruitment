# Recruitment Scheduler

## Opis
Aplikacja do zarządzania rozmowami rekrutacyjnymi (Spring Boot + MongoDB).
Umożliwia dodawanie slotów, rezerwacji, integrację z Google Calendar i stosowanie limitów rekrutera.

## Technologia
- Java 17, Spring Boot
- MongoDB (NoSQL)
- Lombok, MapStruct
- Google Calendar API (OAuth)

## Uruchamianie aplikacji:
1. Zainstaluj i uruchom MongoDB (localhost:27017).
2. W pliku application.properties ustaw:
spring.data.mongodb.uri=mongodb://localhost:27017/recruitmentdb
3. Uruchom aplikację komendą:
mvn spring-boot:run

Aplikacja będzie dostępna pod adresem: http://localhost:8080/.

## Docker Compose
W pliku docker-compose.yml zdefiniowane są usługi:
1. MongoDB (port 27017)
2. Zookeeper (port 2181)
3. Kafka (port 9092)
Uruchom je poleceniem:
docker-compose up -d

## Swagger:
http://localhost:8080/swagger-ui/index.html

