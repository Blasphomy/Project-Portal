# Project Portal - Complete User Journey API Guide

This guide walks through all APIs in the order a user would interact with the platform.

---

## **Phase 1: User Setup & Content Discovery**

### 1.1 Create a User (Registration)
```
POST http://localhost:8086/api/users
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "totalXp": 0
}
```

**Response:**
```json
{
  "id": "user-123",
  "name": "John Doe",
  "email": "john@example.com",
  "totalXp": 0
}
```

Save the `id` for future requests. We'll use `user-123` in examples below.

---

### 1.2 Get User Profile
```
GET http://localhost:8086/api/users/user-123
```

**Response:** User details including total XP and creation date.

---

### 1.3 Browse All Topics
```
GET http://localhost:8086/api/topics
```

**Response:** List of all available topics (Java, SQL, Git, Docker, etc.)

```json
[
  {
    "id": "topic-java-101",
    "name": "Java Basics",
    "description": "Learn Java fundamentals"
  },
  ...
]
```

---

### 1.4 View Full Topic with Quests & Tasks (Complete Hierarchy)
```
GET http://localhost:8086/api/topics/topic-java-101/tree
```

**Response:** Complete nested structure:
```json
{
  "id": "topic-java-101",
  "name": "Java Basics",
  "description": "Learn Java fundamentals",
  "quests": [
    {
      "id": "quest-1",
      "name": "Variables & Data Types",
      "description": "Learn about variables",
      "orderIndex": 1,
      "tasks": [
        {
          "id": "task-1",
          "name": "Declare Variables",
          "description": "Create different variable types",
          "orderIndex": 1,
          "xpReward": 50
        },
        {
          "id": "task-2",
          "name": "Type Casting",
          "description": "Practice type casting",
          "orderIndex": 2,
          "xpReward": 75
        }
      ]
    },
    ...
  ]
}
```

---

## **Phase 2: Starting & Completing Tasks**

### 2.1 Start a Task (User begins working on it)
```
POST http://localhost:8086/api/progress/tasks/task-1/start?userId=user-123
```

**Response:**
```json
{
  "id": "progress-uuid",
  "userId": "user-123",
  "taskId": "task-1",
  "status": "IN_PROGRESS",
  "gainedXp": 0,
  "updatedAt": "2025-12-26T12:00:00"
}
```

**What happens:**
- Task progress is created with `status = "IN_PROGRESS"`
- Quest progress is automatically created for the quest containing this task (if not already created)

---

### 2.2 Complete a Task (User finishes working on it)
```
POST http://localhost:8086/api/progress/tasks/task-1/complete?userId=user-123
```

**Response:**
```json
{
  "id": "progress-uuid",
  "userId": "user-123",
  "taskId": "task-1",
  "status": "COMPLETED",
  "gainedXp": 50,
  "updatedAt": "2025-12-26T12:05:00"
}
```

**What happens:**
- Task marked as `COMPLETED`
- XP reward (50) is added to user's `totalXp`
- Quest progress XP is incremented
- First task badge (`badge-first-task`) is awarded if this is user's 1st task
- If all tasks in the quest are completed, quest is marked as `COMPLETED` and `badge-quest-master` is awarded

---

### 2.3 Check Task Progress
```
GET http://localhost:8086/api/progress/users/user-123/tasks/task-1
```

**Response:** Current status of that specific task for the user.

---

## **Phase 3: Quest-Level Progress**

### 3.1 Check Specific Quest Progress
```
GET http://localhost:8086/api/progress/users/user-123/quests/quest-1
```

**Response:**
```json
{
  "id": "quest-progress-uuid",
  "userId": "user-123",
  "questId": "quest-1",
  "status": "IN_PROGRESS",
  "gainedXp": 125,
  "updatedAt": "2025-12-26T12:15:00"
}
```

---

### 3.2 Get Quest with All Tasks & Progress (Detailed View)
```
GET http://localhost:8086/api/progress/users/user-123/quests/quest-1/with-tasks
```

**Response:** Shows quest details + each task's completion status:
```json
{
  "questId": "quest-1",
  "questProgress": {
    "status": "IN_PROGRESS",
    "gainedXp": 125
  },
  "tasks": [
    {
      "taskId": "task-1",
      "taskTitle": "Declare Variables",
      "status": "COMPLETED",
      "gainedXp": 50
    },
    {
      "taskId": "task-2",
      "taskTitle": "Type Casting",
      "status": "COMPLETED",
      "gainedXp": 75
    }
  ]
}
```

---

### 3.3 List All User's Quest Progress
```
GET http://localhost:8086/api/progress/users/user-123/quests
```

**Response:** Array of all quest progress records for the user.

---

## **Phase 4: User Progress Dashboard**

### 4.1 Get All User's Task Progress
```
GET http://localhost:8086/api/progress/users/user-123/tasks
```

**Response:** Array of all task progress records.

---

### 4.2 Get Overall Completion Status
```
GET http://localhost:8086/api/progress/users/user-123/completion-status
```

**Response:**
```json
{
  "userId": "user-123",
  "totalXp": 3500,
  "tasksCompleted": 10,
  "tasksTotal": 10,
  "questsCompleted": 5,
  "questsTotal": 5,
  "badgesEarned": 8,
  "allTasksCompleted": true,
  "allQuestsCompleted": true,
  "isFullyCompleted": true
}
```

---

## **Phase 5: Achievements & Badges**

### 5.1 View All Available Badges
```
GET http://localhost:8086/api/badges
```

**Response:**
```json
[
  {
    "id": "badge-first-task",
    "name": "First Step",
    "description": "Complete your first task",
    "iconUrl": "/icons/first-step.png"
  },
  {
    "id": "badge-quest-master",
    "name": "Quest Master",
    "description": "Complete an entire quest",
    "iconUrl": "/icons/quest-master.png"
  },
  {
    "id": "badge-legend-master",
    "name": "Legend Master",
    "description": "Complete all tasks and quests - Master of all knowledge!",
    "iconUrl": "/icons/legend-master.png"
  },
  ...
]
```

---

### 5.2 Get User's Earned Badges
```
GET http://localhost:8086/api/users/user-123/badges
```

**Response:** Array of badge objects the user has earned.

---

### 5.3 Award Mastery Badges (When User Completes Everything)
```
POST http://localhost:8086/api/progress/users/user-123/award-mastery
```

**Response:** Updated completion status with mastery badges awarded:
```json
{
  "userId": "user-123",
  "totalXp": 3500,
  "tasksCompleted": 10,
  "tasksTotal": 10,
  "questsCompleted": 5,
  "questsTotal": 5,
  "badgesEarned": 9,
  "allTasksCompleted": true,
  "allQuestsCompleted": true,
  "isFullyCompleted": true
}
```

---

## **Suggested User Journey (Step-by-Step)**

### **Day 1: New User**

1. Register
   ```
   POST /api/users
   ```

2. Browse topics
   ```
   GET /api/topics
   ```

3. View Java topic with all quests/tasks
   ```
   GET /api/topics/topic-java-101/tree
   ```

4. Start first task
   ```
   POST /api/progress/tasks/task-1/start?userId=user-123
   ```

5. Complete first task
   ```
   POST /api/progress/tasks/task-1/complete?userId=user-123
   ```

6. Check profile (XP should increase)
   ```
   GET /api/users/user-123
   ```

7. Check earned badges
   ```
   GET /api/users/user-123/badges
   ```

---

### **Day 2-5: Complete First Quest**

1. Start all remaining tasks in quest-1
   ```
   POST /api/progress/tasks/task-2/start?userId=user-123
   POST /api/progress/tasks/task-3/start?userId=user-123
   ...
   ```

2. Complete all remaining tasks
   ```
   POST /api/progress/tasks/task-2/complete?userId=user-123
   POST /api/progress/tasks/task-3/complete?userId=user-123
   ...
   ```

3. Check quest with all tasks + progress
   ```
   GET /api/progress/users/user-123/quests/quest-1/with-tasks
   ```

4. Check badges (should have `badge-quest-master`)
   ```
   GET /api/users/user-123/badges
   ```

---

### **Week 2-3: Complete All Quests & Tasks**

1. Repeat start/complete cycle for all other quests and tasks

2. Periodically check completion status
   ```
   GET /api/progress/users/user-123/completion-status
   ```

3. When `isFullyCompleted` = `true`, award mastery badges
   ```
   POST /api/progress/users/user-123/award-mastery
   ```

4. View final profile with all stats
   ```
   GET /api/users/user-123
   GET /api/users/user-123/badges
   GET /api/progress/users/user-123/completion-status
   ```

---

## **Complete API Reference**

### **User Management**
- `POST /api/users` - Create user
- `GET /api/users` - List all users (with pagination: `?page=0&size=10`)
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user

### **Content (Topics, Quests, Tasks)**
- `GET /api/topics` - List topics
- `GET /api/topics/{id}` - Get topic
- `GET /api/topics/{id}/tree` - Get topic with nested quests & tasks
- `GET /api/quests` - List quests
- `GET /api/quests/{id}` - Get quest
- `GET /api/topics/{topicId}/quests` - Get quests for topic
- `GET /api/tasks` - List tasks
- `GET /api/tasks/{id}` - Get task
- `GET /api/quests/{questId}/tasks` - Get tasks for quest

### **Progress & Gameplay**
- `POST /api/progress/tasks/{taskId}/start?userId={userId}` - Start task
- `POST /api/progress/tasks/{taskId}/complete?userId={userId}` - Complete task
- `GET /api/progress/users/{userId}/tasks/{taskId}` - Get task progress
- `GET /api/progress/users/{userId}/tasks` - Get all task progress
- `GET /api/progress/users/{userId}/quests/{questId}` - Get quest progress
- `GET /api/progress/users/{userId}/quests` - Get all quest progress
- `GET /api/progress/users/{userId}/quests/{questId}/with-tasks` - Quest + tasks + progress
- `GET /api/progress/users/{userId}/completion-status` - Overall completion stats
- `POST /api/progress/users/{userId}/award-mastery` - Award final badges

### **Badges & Achievements**
- `GET /api/badges` - List all badges
- `GET /api/badges/{id}` - Get badge
- `GET /api/users/{userId}/badges` - Get user's earned badges
- `POST /api/users/{userId}/badges/{badgeId}` - Manually award badge

---

## **Postman Collection Setup**

### **Environment Variables (Set in Postman)**

```
baseUrl = http://localhost:8086
userId = user-123
topicId = topic-java-101
questId = quest-1
taskId = task-1
```

Then use `{{baseUrl}}/api/users/{{userId}}` in your requests.

---

## **Expected Badge Progression**

As user completes content, they earn:

1. **badge-first-task** - Complete 1st task
2. **badge-task-warrior** - Complete 5 tasks
3. **badge-quest-starter** - Complete 1st quest
4. **badge-quest-master** - Complete entire quest (per quest)
5. **badge-task-legend** - Complete 10 tasks
6. **badge-quest-explorer** - Complete 3 quests
7. **badge-quest-god** - Complete all quests
8. **badge-legend-master** - Complete all tasks & quests (final achievement)

---

## **Tips for Testing**

1. **Use multiple users** to test concurrent progress tracking
2. **Seed data includes** 2 quests with 2 tasks each = 4 tasks total
3. **To test mastery badges**, you need to complete all 4 tasks + all 2 quests
4. **Check XP accumulation** after each task completion
5. **Verify quest auto-completion** when all tasks in quest are done


