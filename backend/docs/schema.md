
## Final Schema

**users**
```sql
id            BIGSERIAL PK
name          VARCHAR(100) NOT NULL
email         VARCHAR(255) NOT NULL UNIQUE
password_hash VARCHAR(255) NOT NULL
role          VARCHAR(20) NOT NULL DEFAULT 'USER'
joined_at     TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

**topics**
```sql
id          BIGSERIAL PK
name        VARCHAR(100) NOT NULL UNIQUE
description TEXT
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

**articles**
```sql
id          BIGSERIAL PK
title       VARCHAR(500) NOT NULL
description TEXT
content     TEXT
source_url  TEXT NOT NULL UNIQUE
likes       BIGINT NOT NULL DEFAULT 0
expires_at  TIMESTAMPTZ
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

**article_topics** (junction)
```sql
article_id  BIGINT FK → articles.id ON DELETE CASCADE
topic_id    BIGINT FK → topics.id ON DELETE CASCADE
PRIMARY KEY (article_id, topic_id)
```

**user_topics** (subscriptions)
```sql
user_id     BIGINT FK → users.id ON DELETE CASCADE
topic_id    BIGINT FK → topics.id ON DELETE CASCADE
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
PRIMARY KEY (user_id, topic_id)
```

**article_likes** (prevent duplicate likes)
```sql
user_id     BIGINT FK → users.id ON DELETE CASCADE
article_id  BIGINT FK → articles.id ON DELETE CASCADE
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
PRIMARY KEY (user_id, article_id)
```

**notifications**
```sql
id          BIGSERIAL PK
user_id     BIGINT FK → users.id ON DELETE CASCADE
article_id  BIGINT FK → articles.id ON DELETE CASCADE
read        BOOLEAN NOT NULL DEFAULT FALSE
delivered   BOOLEAN NOT NULL DEFAULT FALSE
created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
```

---

## Indexes to add

```sql
-- hot path: feed load by topic
CREATE INDEX idx_article_topics_topic_id ON article_topics(topic_id);

-- hot path: who is subscribed to a topic (fanout query)
CREATE INDEX idx_user_topics_topic_id ON user_topics(topic_id);

-- hot path: unread notifications per user
CREATE INDEX idx_notifications_user_unread 
    ON notifications(user_id) 
    WHERE read = FALSE;

-- prevent duplicate article ingestion
CREATE UNIQUE INDEX idx_articles_source_url ON articles(source_url);

-- feed ordering
CREATE INDEX idx_articles_created_at ON articles(created_at DESC);
```

---

## Redis Key Design

```redis [newsfeed]
feed:{userId}              → LIST of article IDs     TTL: 4hrs
article:{articleId}        → HASH of article data    TTL: 1hr
topic:{topicId}:articles   → LIST of article IDs     TTL: 2min
topics:all                 → LIST of all topics       TTL: 24hrs
rate:{ipAddress}           → Bucket4j token bucket    TTL: auto
```

---
