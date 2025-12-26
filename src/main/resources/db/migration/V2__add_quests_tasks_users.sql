CREATE TABLE users (
    id          VARCHAR(100) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    name        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    total_xp    INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

CREATE TABLE quests (
    id          VARCHAR(100) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    topic_id    VARCHAR(100) NOT NULL REFERENCES topics(id),
    name        VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT          NOT NULL DEFAULT 0
);

CREATE TABLE tasks (
    id          VARCHAR(100) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    quest_id    VARCHAR(100) NOT NULL REFERENCES quests(id),
    title       VARCHAR(255) NOT NULL,
    description TEXT,
    order_index INT          NOT NULL DEFAULT 0,
    xp_reward   INT          NOT NULL DEFAULT 0
);