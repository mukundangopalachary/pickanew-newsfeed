-- V1__init.sql

CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(100)  NOT NULL,
    email         VARCHAR(255)  NOT NULL UNIQUE,
    password_hash VARCHAR(255)  NOT NULL,
    role          VARCHAR(20)   NOT NULL DEFAULT 'USER',
    joined_at     TIMESTAMPTZ   NOT NULL DEFAULT NOW()
);

CREATE TABLE topics (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

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

CREATE TABLE article_topics (
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    topic_id    BIGINT NOT NULL REFERENCES topics(id)   ON DELETE CASCADE,
    PRIMARY KEY (article_id, topic_id)
);

CREATE TABLE user_topics (
    user_id     BIGINT NOT NULL REFERENCES users(id)  ON DELETE CASCADE,
    topic_id    BIGINT NOT NULL REFERENCES topics(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, topic_id)
);

CREATE TABLE article_likes (
    user_id     BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, article_id)
);

CREATE TABLE notifications (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id)    ON DELETE CASCADE,
    article_id  BIGINT NOT NULL REFERENCES articles(id) ON DELETE CASCADE,
    read        BOOLEAN     NOT NULL DEFAULT FALSE,
    delivered   BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Indexes

-- hot path: fetch all topics for an article (feed display)
CREATE INDEX idx_article_topics_topic_id ON article_topics(topic_id);

-- hot path: fanout — find all subscribers for a topic
CREATE INDEX idx_user_topics_topic_id ON user_topics(topic_id);

-- hot path: unread notifications per user
CREATE INDEX idx_notifications_user_unread
    ON notifications(user_id)
    WHERE read = FALSE;

-- feed ordering (newest first)
CREATE INDEX idx_articles_created_at ON articles(created_at DESC);

-- expiry cleanup job
CREATE INDEX idx_articles_expires_at
    ON articles(expires_at)
    WHERE expires_at IS NOT NULL;
