-- Create tables for Learning Platform

CREATE TABLE IF NOT EXISTS topics (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    icon_url VARCHAR(512),
    difficulty_level VARCHAR(50),
    estimated_hours INTEGER,
    prerequisites TEXT,
    category VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS quests (
    id VARCHAR(255) PRIMARY KEY,
    topic_id VARCHAR(255) REFERENCES topics(id),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    sequence_order INTEGER,
    difficulty VARCHAR(50),
    estimated_minutes INTEGER,
    learning_objectives TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tasks (
    id VARCHAR(255) PRIMARY KEY,
    quest_id VARCHAR(255) REFERENCES quests(id),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    xp_reward INTEGER DEFAULT 0,
    sequence_order INTEGER,
    has_code_challenge BOOLEAN DEFAULT FALSE,
    starter_code TEXT,
    solution_code TEXT,
    test_cases TEXT,
    hints TEXT,
    difficulty VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS user_progress (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    task_id VARCHAR(255) REFERENCES tasks(id),
    status VARCHAR(50) DEFAULT 'NOT_STARTED',
    code_submission TEXT,
    xp_earned INTEGER DEFAULT 0,
    attempts INTEGER DEFAULT 0,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO topics (id, name, description, category, difficulty_level, estimated_hours) VALUES
('topic-java-101', 'Java Fundamentals', 'Learn the basics of Java programming', 'Programming', 'BEGINNER', 20),
('topic-spring-boot', 'Spring Boot Basics', 'Introduction to Spring Boot framework', 'Backend', 'INTERMEDIATE', 30)
ON CONFLICT (id) DO NOTHING;

INSERT INTO quests (id, topic_id, name, description, sequence_order, difficulty, estimated_minutes) VALUES
('quest-1', 'topic-java-101', 'Hello World', 'Your first Java program', 1, 'EASY', 30),
('quest-2', 'topic-java-101', 'Variables and Types', 'Understanding Java data types', 2, 'EASY', 45)
ON CONFLICT (id) DO NOTHING;

INSERT INTO tasks (id, quest_id, title, description, xp_reward, sequence_order, has_code_challenge) VALUES
('task-1', 'quest-1', 'Print Hello World', 'Write a program that prints Hello World', 10, 1, true),
('task-2', 'quest-1', 'Variables', 'Declare and initialize variables', 15, 2, true)
ON CONFLICT (id) DO NOTHING;
