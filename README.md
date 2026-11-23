# Order Platform — Spring Boot Microservices

A small order/product system built to practice **testing, containerization and service-to-service communication** with Spring Boot.

![CI](https://github.com/<github-username>/siparis-platformu/actions/workflows/ci.yml/badge.svg)

## Architecture

```
                 REST (sync)
 order-service  ───────────────►  product-service
    :8082        GET /api/products/{id}   :8081
      │                                     ▲
      │  order.created (async)              │
      └──────►  RabbitMQ (order.events) ────┘  decreases stock
      │                                     │
   orders DB          PostgreSQL        products DB
```

- **order-service** checks price and stock via REST, saves the order, publishes `OrderCreatedEvent`.
- **product-service** consumes the event and decreases stock.
- Each service owns its own database (database-per-service).

## Tech stack

Java 21 · Spring Boot 3 · Spring Data JPA · Spring AMQP (RabbitMQ) · PostgreSQL ·
JUnit 5 · Mockito · MockMvc · H2 · JaCoCo · Docker (multi-stage) · Docker Compose · Jenkins · GitHub Actions

## Run

```bash
docker compose up --build
```

```bash
# create a product
curl -X POST localhost:8081/api/products -H "Content-Type: application/json" \
     -d '{"name":"Keyboard","price":750.00,"stock":10}'

# place an order
curl -X POST localhost:8082/api/orders -H "Content-Type: application/json" \
     -d '{"productId":1,"quantity":3}'

# stock is now 7
curl localhost:8081/api/products/1
```

RabbitMQ management UI: http://localhost:15672 (app / app)

## Tests

```bash
mvn clean verify          # unit + integration tests + coverage
# coverage report: <service>/target/site/jacoco/index.html
```

| Level | Tool | Example |
|-------|------|---------|
| Unit | JUnit 5 + Mockito | `ProductServiceTest`, `OrderServiceTest` |
| Web slice | `@WebMvcTest` + MockMvc | `ProductControllerTest` |
| REST client | `@RestClientTest` | `ProductClientTest` |
| Integration | `@SpringBootTest` + H2 | `ProductIntegrationTest` |

## Design decisions

- **REST for the stock check, messaging for the stock update:** the order needs an immediate answer
  (price, availability), while the stock update can happen asynchronously and survive a temporary outage of product-service.
- **Multi-stage Docker build:** the final image contains only the JRE and the jar.
- Errors are returned as RFC 7807 `ProblemDetail`.

## Known limitations / what I'd improve next

- Saving the order and publishing the event are not atomic → **Transactional Outbox**.
- Two concurrent orders can pass the stock check → optimistic locking / stock reservation.
- No retry / circuit breaker on the REST call → Resilience4j.
- Integration tests use H2 → Testcontainers with real PostgreSQL and RabbitMQ.
- Kubernetes manifests (minikube).
