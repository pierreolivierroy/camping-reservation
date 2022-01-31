# Camping Reservation
This is a spring-boot application that lets you reserve a camping. It also uses a Postgres database and RabbitMQ.

## 1. Run the services
First, build the two applications:
```
cd availability-service
mvn clean package
cd ..
cd reservation-service
mvn clean package
```
On the project root, run the following command:
```
docker-compose up
```
Both availability and reservation services should start running along with the database and the message broker.


## 2. Try the APIs

### Get campsite availabilities
```
curl --request GET 'http://localhost:8081/api/availability/v1.1?searchStartDate=2022-02-10T00:00:00.000Z&searchEndDate=2022-02-20T00:00:00.000Z'
```

### Reserve the campsite for specified dates
```
curl --request POST 'http://localhost:8080/api/reservation/v1.1' \
--header 'Content-Type: application/json' \
--data-raw '{
    "guestEmail": "jean.paul@email.com",
    "firstName": "Jean",
    "lastName": "Paul",
    "arrivalDate": "2022-02-02T12:00:00.000Z",
    "departureDate": "2022-02-03T19:01:00.000Z"
}'
```

### Get all reservations for an email
```
curl --request GET 'http://localhost:8080/api/reservation/v1.1?email=jean.paul@email.com'
```

### Update an existing reservation's date
```
curl --request PATCH 'http://localhost:8080/api/reservation/v1.1/{reservationId}' \
--header 'Content-Type: application/json' \
--data-raw '{
    "arrivalDate": "2022-02-02T12:00:00.000Z",
    "departureDate": "2022-02-04T19:01:00.000Z"
}'
```

### Cancel an existing reservation
```
curl --request DELETE 'http://localhost:8080/api/reservation/v1.1/{reservationId}'
```

## 3. Shutdown the services
On the project root, run the following command:
```
docker-compose down
```
