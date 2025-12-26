CREATE TABLE user_quest_progress (
    id          VARCHAR(100) PRIMARY KEY DEFAULT gen_random_uuid()::text,
    user_id     VARCHAR(100) NOT NULL REFERENCES users(id),
    quest_id    VARCHAR(100) NOT NULL REFERENCES quests(id),
    status      VARCHAR(50)  NOT NULL, -- e.g. IN_PROGRESS, COMPLETED
    gained_xp   INT          NOT NULL DEFAULT 0,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE (user_id, quest_id)
);