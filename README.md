# RabbitMQ Inter-Process Communication

Two Spring Boot applications that communicate asynchronously through RabbitMQ. The Producer exposes a REST endpoint that publishes user messages to a RabbitMQ exchange; the Consumer listens on the bound queue, persists each message to PostgreSQL, and exposes a REST endpoint to retrieve the stored messages.

## Architecture

```
Client (Postman)
      |
      | POST /api/send
      v
Producer Service (:8081)
      |
      | publish (routing key: user.routingkey)
      v
  user.exchange  -->  user.queue
                          |
                          | consume
                          v
                 Consumer Service (:8080)
                          |
                          | persist
                          v
                     PostgreSQL
                          ^
                          | read
      GET /api/messages ---
```

The Producer never talks to the Consumer directly. It publishes to an exchange, which routes the message to a queue based on the routing key. The Consumer listens on that queue. This decouples the two services: neither needs to know the other exists.

## Tech Stack

**Producer Service**
- Java 25, Spring Boot 4.1.0
- Spring Web, Spring AMQP, Bean Validation
- No database

**Consumer Service**
- Java 21, Spring Boot 3.5.0
- Spring Web, Spring AMQP, Spring Data JPA
- PostgreSQL 16, Flyway migrations

**Infrastructure**
- RabbitMQ 3 (with management UI) via Docker
- PostgreSQL 16 via Docker

## Prerequisites

- Java 21 or higher
- Maven
- Docker and Docker Compose

## Setup

### 1. Start RabbitMQ

From the `producer-service` directory:

```bash
docker compose up -d
```

This starts RabbitMQ with two exposed ports:
- `5672` — AMQP protocol port (used by the applications)
- `15672` — management web UI

Verify it is running by opening http://localhost:15672 and logging in with `guest` / `guest`.

### 2. Start PostgreSQL

From the `consumer-service` directory, create a `.env` file with the database credentials:

```
POSTGRES_DB=fee_engine
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
```

Then start the database:

```bash
docker compose up -d
```

The Consumer's `application.properties` must use the same credentials.

### 3. RabbitMQ Configuration

The exchange, queue, and binding are declared programmatically in both applications (`RabbitMQConfig`), so no manual setup is required. On startup each application declares:

| Component | Name | Type |
|-----------|------|------|
| Exchange | `user.exchange` | direct |
| Queue | `user.queue` | classic |
| Binding | routing key `user.routingkey` | queue bound to exchange |

Messages are serialized as JSON so they are readable in the management UI and language-neutral.

### 4. Run the applications

Start each application from its own directory:

```bash
# Consumer (port 8080)
cd consumer-service
mvn spring-boot:run

# Producer (port 8081) -- in a separate terminal
cd producer-service
mvn spring-boot:run
```

Both must be running for the full flow to work.

## Endpoints

### Producer Service (http://localhost:8081)

**POST /api/send**

Builds a user message with the supplied name and email plus a server-generated timestamp, publishes it to RabbitMQ, and returns the published message.

Request body:
```json
{
  "name": "Hardy",
  "email": "hardy@gateway.com"
}
```

Response (200 OK):
```json
{
  "name": "Hardy",
  "email": "hardy@gateway.com",
  "timestamp": "2026-08-04T10:28:04.7218044"
}
```

Both fields are required. `name` must not be blank, and `email` must be a valid email address. Invalid input returns 400 Bad Request.

### Consumer Service (http://localhost:8080)

**GET /api/messages**

Returns the stored messages, paginated.

Optional query parameters:
- `page` — page number, zero-indexed (default `0`)
- `size` — items per page (default `20`)

Response (200 OK):
```json
{
  "content": [
    {
      "name": "Hardy",
      "email": "hardy@gateway.com",
      "sentAt": "2026-08-04T10:28:04.721804"
    }
  ],
  "pageable": { "pageNumber": 0, "pageSize": 20 },
  "totalElements": 1,
  "totalPages": 1
}
```

## Testing the Flow

1. Ensure RabbitMQ and PostgreSQL are running (`docker ps`) and both applications have started.

2. Send a message:
   ```bash
   curl -X POST http://localhost:8081/api/send \
     -H "Content-Type: application/json" \
     -d "{\"name\":\"Hardy\",\"email\":\"hardy@gateway.com\"}"
   ```
   Expect a 200 response echoing the message with its timestamp.

3. Check the Consumer's console. The listener logs each message as it arrives:
   ```
   Received message: UserMessageRequest[name=Hardy, email=hardy@gateway.com, timestamp=...]
   ```

4. Retrieve the stored messages:
   ```bash
   curl http://localhost:8080/api/messages
   ```
   The message should appear in the `content` array, with `sentAt` matching the timestamp from step 2.

### Inspecting RabbitMQ

The management UI at http://localhost:15672 is useful for debugging. Under **Queues and Streams**, `user.queue` shows how many messages are ready or unacknowledged. If the Consumer is running, messages are consumed almost immediately and the queue stays empty. Stopping the Consumer and sending messages will show them accumulating in the queue, then draining once the Consumer restarts, which demonstrates the decoupling between the two services.

## Project Structure

```
producer-service/
  src/main/java/.../config/RabbitMQConfig.java      exchange, queue, binding, JSON converter
  src/main/java/.../model/UserMessage.java          the published message
  src/main/java/.../service/MessageProducerService.java   builds and publishes
  src/main/java/.../controller/MessageController.java     POST /api/send
  docker-compose.yml                                RabbitMQ

consumer-service/
  src/main/java/.../config/RabbitMQConfig.java              queue, binding, JSON converter
  src/main/java/.../listener/UserMessageListener.java       @RabbitListener, persists messages
  src/main/java/.../persistence/entity/UserMessageEntity.java
  src/main/java/.../persistence/repository/UserMessageRepository.java
  src/main/java/.../service/UserMessageService.java
  src/main/java/.../controller/UserMessageController.java   GET /api/messages
  src/main/resources/db/migration/                          Flyway migrations
  docker-compose.yml                                        PostgreSQL
```

## Notes

The Consumer is built on an existing Spring Boot backend (a fee calculation engine) that already provided the PostgreSQL setup, Flyway migrations, and JPA infrastructure. The messaging components listed above are what this assignment added; the remaining code belongs to that earlier project.

Database credentials are supplied via a `.env` file which is not committed. See `.env.example` for the required variables.
