-- Insert Java topic
INSERT INTO topics (id,name, description) VALUES
('topic-java-101', 'Java basics', 'Learn syntax, types, and OOP.'),
('topic-java-102', 'Spring Boot', 'Build REST APIs and services.'),
('topic-java-103', 'SQL & Databases', 'Work with relational data.');

-- Insert quests for Java topic
INSERT INTO quests (id,topic_id, name, description, order_index)
VALUES
  ('quest-1','topic-java-101', 'Variables & Data Types', 'Learn about variables', 1),
  ('quest-2','topic-java-101', 'Control Flow', 'Learn if/else and loops', 2);

-- Insert tasks for Quest 1
INSERT INTO tasks (id,quest_id, title, description, xp_reward, order_index)
VALUES
  ('task-1','quest-1', 'Declare Variables', 'Create different variable types', 50, 1),
  ('task-2','quest-1', 'Type Casting', 'Practice type casting', 75, 2);

-- Insert tasks for Quest 2
INSERT INTO tasks (id,quest_id, title, description, xp_reward, order_index)
VALUES
  ('task-3','quest-2', 'If/Else Statements', 'Write conditional logic', 100, 1),
  ('task-4','quest-2', 'For Loops', 'Implement loop structures', 125, 2);

-- Insert sample badges
INSERT INTO badges (id, name, description, icon_url)
VALUES
  ('badge-1', 'First Step', 'Complete your first task', '/icons/first-step.png'),
  ('badge-2', 'Quest Master', 'Complete an entire quest', '/icons/quest-master.png'),
  ('badge-3', 'Task Warrior', 'Complete 5 tasks', '/icons/task-warrior.png'),
  ('badge-4', 'Task Legend', 'Complete 10 tasks', '/icons/task-legend.png'),
  ('badge-5', 'Quest Starter', 'Complete your first quest', '/icons/quest-starter.png'),
  ('badge-6', 'Quest Explorer', 'Complete 3 quests', '/icons/quest-explorer.png'),
  ('badge-7', 'Quest God', 'Complete all quests', '/icons/quest-god.png'),
  ('badge-8', 'Legend Master', 'Complete all tasks and quests - Master of all knowledge!', '/icons/legend-master.png'),
  ('badge-9', 'Java Master', 'Complete all Java quests', '/icons/java-master.png');

-- Insert sample user
INSERT INTO users (id,name, email, total_xp)
VALUES ('user-1','John Doe', 'john@example.com', 0);