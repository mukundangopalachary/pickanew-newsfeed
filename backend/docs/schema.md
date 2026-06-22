## Database Schema

### users

```sql
CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255),
    role          VARCHAR(20)   NOT NULL,
    provider      VARCHAR(20)   NOT NULL,
    joined_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW(),
    enabled       BOOLEAN       NOT NULL DEFAULT TRUE
);
```

`password_hash` is nullable — OAuth users (Google) never set a local password.

### topics

```sql
CREATE TABLE topics (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

### articles

```sql
CREATE TABLE articles (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(500) NOT NULL,
    description TEXT,
    content     TEXT,
    source_url  TEXT         NOT NULL UNIQUE,
    likes       BIGINT       NOT NULL DEFAULT 0,
    expires_at  TIMESTAMPTZ,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);
```

### article_topics (junction)

```sql
CREATE TABLE article_topics (
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    topic_id    BIGINT NOT NULL REFERENCES topics(id)   ON DELETE CASCADE,
    PRIMARY KEY (article_id, topic_id)
);
```

### user_topics (subscriptions)

```sql
CREATE TABLE user_topics (
    user_id     BIGINT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    topic_id    BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, topic_id)
);
```

### article_likes

```sql
CREATE TABLE article_likes (
    user_id     BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, article_id)
);
```

### notifications

```sql
CREATE TABLE notifications (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    read        BOOLEAN     NOT NULL DEFAULT FALSE,
    delivered   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
```

### Indexes

```sql
CREATE INDEX idx_article_topics_topic_id ON article_topics(topic_id);
CREATE INDEX idx_user_topics_topic_id ON user_topics(topic_id);
CREATE INDEX idx_notifications_user_unread ON notifications(user_id) WHERE read = FALSE;
CREATE INDEX idx_articles_created_at ON articles(created_at DESC);
CREATE INDEX idx_articles_expires_at ON articles(expires_at) WHERE expires_at IS NOT NULL;
```

### Redis Key Design

```
feed:{userId}              → LIST of article IDs     TTL: 4hrs
article:{articleId}        → HASH of article data    TTL: 1hr
topic:{topicId}:articles   → LIST of article IDs     TTL: 2min
topics:all                 → LIST of all topics       TTL: 24hrs
rate:{userId}              → Bucket4j token bucket    TTL: auto
```

### Migration History

- `V1__init.sql` — all tables + indexes
- `V2__seed_topics.sql` — seed tech, sports, science, business, health
- `V3__fix_user_constraints.sql` — name/email NOT NULL, password_hash nullable (OAuth support)

---
