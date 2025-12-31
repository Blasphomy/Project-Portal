-- =============================================
-- User Service Database Indexes
-- =============================================

-- Index on email for login queries
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Index on username for profile lookups
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- Index on created_at for sorting/filtering
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

-- Composite index for active users
CREATE INDEX IF NOT EXISTS idx_users_active_created ON users(is_active, created_at);

-- =============================================
-- Learning Service Database Indexes
-- =============================================

-- Index on topic_id for quest lookups
CREATE INDEX IF NOT EXISTS idx_quests_topic_id ON quests(topic_id);

-- Index on quest_id for task lookups
CREATE INDEX IF NOT EXISTS idx_tasks_quest_id ON tasks(quest_id);

-- Index on user progress queries
CREATE INDEX IF NOT EXISTS idx_user_progress_user_id ON user_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_user_progress_quest_id ON user_progress(quest_id);

-- Composite index for active quests by difficulty
CREATE INDEX IF NOT EXISTS idx_quests_difficulty_active ON quests(difficulty, is_active);

-- Index on completion timestamps
CREATE INDEX IF NOT EXISTS idx_user_progress_completed_at ON user_progress(completed_at);

-- Analyze tables after creating indexes
ANALYZE users;
ANALYZE quests;
ANALYZE tasks;
ANALYZE user_progress;
