# Event Management Kafka POC (Spring Boot 3 + Kafka + H2)

A small event-management API where every business action is published to Kafka,
and a consumer in the same app reads it back, stores an audit row in H2 and "sends a notification" (log line).

```
REST call --> EventService --(save)--> H2 (events, registrations)
                   |
                   +--> EventProducer --> Kafka topic "event-management.events" (3 partitions, key = eventId)
                                                   |
                                                   v
                                         EventConsumer --> H2 (event_logs) + [notify] log line
```

Kafka message types: `EVENT_CREATED`, `ATTENDEE_REGISTERED`, `EVENT_CANCELLED`.

## 1. Start Kafka (Docker needed)

    docker compose up -d

Single Kafka broker in KRaft mode (no Zookeeper) on `localhost:9092`.

## 2. Run the app
Open the folder in IntelliJ (Maven project, Java 17) and run `EventManagementApplication`,
or `mvn spring-boot:run`. The topic is created automatically.

H2 console: http://localhost:8080/h2-console , JDBC URL `jdbc:h2:mem:eventdb`, user `sa`, empty password.

## 3. Try it (Postman or curl)

    # create an event (use a future date)
    curl -X POST http://localhost:8080/api/events -H "Content-Type: application/json" \
      -d '{"name":"Java Meetup","venue":"Lucknow","eventDate":"2027-01-15T18:00:00","capacity":2}'

    # register attendees
    curl -X POST http://localhost:8080/api/events/1/register -H "Content-Type: application/json" \
      -d '{"attendeeName":"Ravi","attendeeEmail":"ravi@example.com"}'

    # cancel the event
    curl -X POST http://localhost:8080/api/events/1/cancel

    # see what the Kafka consumer received and stored
    curl http://localhost:8080/api/event-logs

Watch the app console for `Published ...` (producer) and `[notify] ...` (consumer) lines.

Watch the raw topic from a terminal:

    docker exec -it kafka /opt/kafka/bin/kafka-console-consumer.sh \
      --bootstrap-server localhost:9092 --topic event-management.events --from-beginning

## Business rules in the POC
Event full -> 409, duplicate email for the same event -> 409, registering for a cancelled event -> 409,
past event date -> 400.

## Stop
`Ctrl+C` the app, then `docker compose down`.

## Next steps
- Outbox pattern so DB save and Kafka publish cannot drift apart if Kafka is down.
- Retry + dead-letter topic for failing consumers (`DefaultErrorHandler`, `DeadLetterPublishingRecoverer`).
- Split the consumer into its own service (notification-service) and run both in the microservices demo.
- JSON serializers + schema registry instead of manual JSON strings.
