## Roadmap

### Phase 1 — Project Setup (DONE)

- Spring Boot scaffolded (web, security, jpa, redis, kafka, flyway)
- Next.js scaffolded (TypeScript, Tailwind)
- Docker Compose — PostgreSQL + Redis running
- Fixed Spring Boot 4.x Flyway starter issue (`spring-boot-starter-flyway`)

### Phase 2 — Database Schema (DONE)

- V1 schema created
- V2 seed topics applied
- V3 user constraints fixed for OAuth support

### Phase 3 — Auth

- `User` entity finalized (OAuth + local support)
- `AuthProvider` enum (LOCAL, GOOGLE)
- `Role` enum
- [ ] `POST /api/auth/register` — local signup
- [ ] `POST /api/auth/login` — returns JWT
- [ ] Google OAuth2 login flow
- [ ] JWT filter — validate token on protected routes
- [ ] Spring Security config — public vs protected routes
- [ ] Next.js — login/register pages, Google login button, store JWT in httpOnly cookie

### Phase 4 — Topics + Subscriptions

- `GET /api/topics`
- `POST /api/subscriptions/:topicId`
- `DELETE /api/subscriptions/:topicId`
- `GET /api/subscriptions`
- Next.js — topics page, subscribe/unsubscribe UI

### Phase 5 — Python Crawler

- RSS parser (feedparser) — BBC, HackerNews, Reuters
- Simple scraper (BeautifulSoup) for one additional source
- Deduplicate by `source_url`
- Publish to Kafka topic `articles.raw`
- Dockerize crawler

### Phase 6 — Kafka Consumer + Article Ingestion

- Spring Kafka consumer setup
- Consume `articles.raw`, persist to PostgreSQL
- Map topics to `article_topics`
- Handle duplicate `source_url` gracefully
- Cache article in Redis on ingestion

### Phase 7 — Feed Generation (Fanout on Write)

- On ingestion, look up subscribers per topic
- Push article ID to `feed:{userId}` in Redis
- Trim feed list to last 100 articles
- `GET /api/feed` — paginated, Redis-first with DB fallback
- Next.js — feed page with infinite scroll

### Phase 8 — Real-time Notifications (SSE)

- Identify online subscribers on ingestion
- `GET /api/stream/sse` with `SseEmitter`
- Next.js — `EventSource` client, toast on new article

### Phase 9 — WebSocket

- `GET /api/stream/ws`
- Same notification flow via WebSocket
- Next.js — native `WebSocket` client
- Compare SSE vs WebSocket firsthand

### Phase 10 — Likes

- `POST /api/articles/:id/like`
- `DELETE /api/articles/:id/like`
- Publish to Kafka topic `articles.likes`
- Atomic counter increment via consumer
- Prevent duplicates via `article_likes`

### Phase 11 — Notifications History

- `GET /api/notifications` — paginated
- `PATCH /api/notifications/:id/read`
- `PATCH /api/notifications/read-all`
- Next.js — notifications page

### Phase 12 — Offline Delivery

- Create `notifications` rows with `delivered = false` for offline users
- Flush on reconnect (SSE/WebSocket)
- Mark `delivered = true` after flush

### Phase 13 — NGINX

- Reverse proxy for Spring Boot
- Serve Next.js static build
- WebSocket proxying config
- SSE proxying config (`proxy_buffering off`)
- Add to Docker Compose

### Phase 14 — Rate Limiting

- Bucket4j + Redis, per authenticated user
- Apply to feed + notification endpoints
- 429 with retry-after header

### Phase 15 — Hardening

- Global exception handler (`@ControllerAdvice`)
- Input validation (`@Valid`)
- Structured logging with request IDs
- Article expiry cleanup job (`@Scheduled`) + Redis eviction
- Integration tests (Testcontainers — PostgreSQL + Kafka + Redis)
- Production Docker Compose config
