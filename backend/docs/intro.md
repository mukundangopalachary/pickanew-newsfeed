## Tech Stack

- **Frontend:** Next.js 14+ (TypeScript + Tailwind CSS)
- **Backend:** Java 21 + Spring Boot 3.x
- **Crawler:** Python 3.x (feedparser + BeautifulSoup + kafka-python)
- **Message Bus:** Apache Kafka
- **Database:** PostgreSQL 16
- **Cache:** Redis 7
- **Migrations:** Flyway
- **Auth:** Spring Security + JWT
- **Reverse Proxy:** NGINX
- **Build:** Maven
- **Containers:** Docker + Docker Compose

---

## Schemas :

[[Schemas]]

## Roadmap

### Phase 1 — Project Setup

- Scaffold Spring Boot project (web, security, jpa, kafka, redis, flyway)
- Scaffold Next.js project (TypeScript, Tailwind)
- Set up Docker Compose with PostgreSQL, Redis, Kafka, Zookeeper, NGINX
- Verify all services start and connect

### Phase 2 — Database Schema

- Write Flyway migrations for all tables
- Verify schema with `\dt` in psql
- Seed topics (tech, sports, science, business, health)

### Phase 3 — Auth

- Implement `POST /api/auth/register`
- Implement `POST /api/auth/login` — returns JWT
- Configure Spring Security — public vs protected routes
- JWT filter — validate token on every protected request
- Next.js — login/register pages, store JWT in httpOnly cookie

### Phase 4 — Topics + Subscriptions

- `GET /api/topics` — list all topics
- `POST /api/subscriptions/:topicId` — subscribe
- `DELETE /api/subscriptions/:topicId` — unsubscribe
- `GET /api/subscriptions` — get my subscriptions
- Next.js — topics page, subscribe/unsubscribe UI

### Phase 5 — Python Crawler

- Set up Python project structure
- Implement RSS feed parser (feedparser) for BBC, HackerNews, Reuters
- Implement simple scraper (BeautifulSoup) for one additional source
- Deduplicate by `source_url` before publishing
- Publish structured article events to Kafka topic `articles.raw`
- Dockerize the crawler

### Phase 6 — Kafka Consumer + Article Ingestion

- Configure Spring Kafka consumer
- Consume from `articles.raw` topic
- Persist articles to PostgreSQL
- Map topics from article metadata to `article_topics` table
- Handle duplicates gracefully (`source_url` unique constraint)
- Cache article in Redis on ingestion

### Phase 7 — Feed Generation (Fanout on Write)

- On article ingestion, look up all subscribers for each topic
- Push article ID into each subscriber's Redis feed list `feed:{userId}`
- Trim feed list to last 100 articles (no infinite growth)
- `GET /api/feed` — return paginated feed from Redis, fall back to DB on miss
- Next.js — feed page, infinite scroll or pagination

### Phase 8 — Real-time Notifications (SSE)

- On article ingestion, identify online subscribers
- Push SSE notification to connected users
- `GET /api/stream/sse` — SSE endpoint with `SseEmitter`
- Next.js — connect via `EventSource`, show toast notification on new article

### Phase 9 — WebSocket

- `GET /api/stream/ws` — WebSocket endpoint
- Implement same notification flow via WebSocket
- Next.js — connect via native `WebSocket` API
- Compare SSE vs WebSocket behavior firsthand

### Phase 10 — Likes

- `POST /api/articles/:id/like` — like an article
- `DELETE /api/articles/:id/like` — unlike
- Publish like event to Kafka topic `articles.likes`
- Consumer increments `likes` counter atomically
- Prevent duplicate likes via `article_likes` table

### Phase 11 — Notifications History

- `GET /api/notifications` — paginated notification history
- `PATCH /api/notifications/:id/read` — mark as read
- `PATCH /api/notifications/read-all` — mark all as read
- Next.js — notifications page with read/unread state

### Phase 12 — Offline Delivery

- On article ingestion, for offline users create `notifications` rows with `delivered = false`
- On user reconnect (SSE/WebSocket), flush undelivered notifications
- Mark as `delivered = true` after flush

### Phase 13 — NGINX

- Configure NGINX as reverse proxy for Spring Boot
- Serve Next.js static build via NGINX
- Configure WebSocket proxying (`proxy_http_version 1.1`, `Upgrade`, `Connection` headers)
- Configure SSE proxying (`proxy_buffering off`)
- Add to Docker Compose

### Phase 14 — Rate Limiting

- Bucket4j + Redis on feed and notification endpoints
- Per user (authenticated) not per IP — since users are logged in
- Return 429 with retry-after header

### Phase 15 — Hardening

- Global exception handler (`@ControllerAdvice`)
- Input validation (`@Valid` on all request bodies)
- Structured logging with request IDs
- Article expiry cleanup job (`@Scheduled`)
- Integration tests with Testcontainers (PostgreSQL + Kafka + Redis)
- Docker Compose production config
