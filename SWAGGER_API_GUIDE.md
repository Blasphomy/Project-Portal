# 🎯 Swagger API Documentation Guide

## Quick Access

**Swagger UI Available At:**
- 🌐 **Development**: http://localhost:8086/swagger-ui.html
- 📋 **OpenAPI JSON**: http://localhost:8086/v3/api-docs
- 📄 **OpenAPI YAML**: http://localhost:8086/v3/api-docs.yaml

---

## 📚 API Endpoints Overview

### Progress Management
Base URL: `/api/progress`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/start-task/{taskId}` | Start a task for a user |
| POST | `/complete-task/{taskId}` | Mark task as completed |
| GET | `/task-progress/{userId}/{taskId}` | Get task progress |
| GET | `/quest-progress/{userId}/{questId}` | Get quest progress |
| GET | `/user/{userId}/quests` | Get all quest progress for user |
| GET | `/user/{userId}/tasks` | Get all task progress for user |
| GET | `/user/{userId}/quest/{questId}/tasks` | Get quest with tasks |
| GET | `/status/{userId}` | Get user completion status |
| POST | `/mastery-badges/{userId}` | Award mastery badges |

### User Management
Base URL: `/api/users`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all users |
| GET | `/{id}` | Get user by ID |
| POST | `/` | Create new user |
| PUT | `/{id}` | Update user |
| DELETE | `/{id}` | Delete user |

### Topics
Base URL: `/api/topics`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all topics (paginated) |
| GET | `/{id}` | Get topic by ID |
| GET | `/{id}/tree` | Get topic hierarchy |
| POST | `/` | Create topic |
| PUT | `/{id}` | Update topic |
| DELETE | `/{id}` | Delete topic |

### Quests
Base URL: `/api/quests`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all quests |
| GET | `/{id}` | Get quest by ID |
| GET | `/topic/{topicId}` | Get quests by topic |
| POST | `/` | Create quest |
| PUT | `/{id}` | Update quest |
| DELETE | `/{id}` | Delete quest |

### Tasks
Base URL: `/api/tasks`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all tasks |
| GET | `/{id}` | Get task by ID |
| GET | `/quest/{questId}` | Get tasks by quest |
| POST | `/` | Create task |
| PUT | `/{id}` | Update task |
| DELETE | `/{id}` | Delete task |

### Badges
Base URL: `/api/badges`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all badges |
| GET | `/{id}` | Get badge by ID |
| POST | `/` | Create badge |
| GET | `/user/{userId}` | Get user badges |

---

## 🎮 API Usage Examples

### 1. Create a User
**Endpoint:** `POST /api/users`

**Request:**
```bash
curl -X POST http://localhost:8086/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "totalXp": 0
  }'
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

---

### 2. Get All Topics with Pagination
**Endpoint:** `GET /api/topics?page=0&size=10`

**Request:**
```bash
curl http://localhost:8086/api/topics?page=0&size=10
```

**Response:**
```json
[
  {
    "id": "topic-java-101",
    "name": "Java basics",
    "description": "Learn syntax, types, and OOP."
  },
  {
    "id": "topic-java-102",
    "name": "Spring Boot",
    "description": "Build REST APIs and services."
  }
]
```

---

### 3. Get Topic Hierarchy
**Endpoint:** `GET /api/topics/{topicId}/tree`

**Request:**
```bash
curl http://localhost:8086/api/topics/topic-java-101/tree
```

**Response:**
```json
{
  "id": "topic-java-101",
  "name": "Java basics",
  "description": "Learn syntax, types, and OOP.",
  "quests": [
    {
      "id": "quest-1",
      "name": "Variables & Data Types",
      "description": "Learn about variables",
      "orderIndex": 1,
      "tasks": [
        {
          "id": "task-1",
          "title": "Declare Variables",
          "description": "Create different variable types",
          "orderIndex": 1,
          "xpReward": 50
        }
      ]
    }
  ]
}
```

---

### 4. Start a Task
**Endpoint:** `POST /api/progress/start-task/{taskId}?userId={userId}`

**Request:**
```bash
curl -X POST "http://localhost:8086/api/progress/start-task/task-1?userId=user-123"
```

**Response:**
```json
{
  "id": "progress-uuid",
  "userId": "user-123",
  "taskId": "task-1",
  "status": "IN_PROGRESS",
  "gainedXp": 0,
  "updatedAt": "2025-12-26T15:10:29.875+05:30"
}
```

---

### 5. Complete a Task
**Endpoint:** `POST /api/progress/complete-task/{taskId}?userId={userId}`

**Request:**
```bash
curl -X POST "http://localhost:8086/api/progress/complete-task/task-1?userId=user-123"
```

**Response:**
```json
{
  "id": "progress-uuid",
  "userId": "user-123",
  "taskId": "task-1",
  "status": "COMPLETED",
  "gainedXp": 50,
  "updatedAt": "2025-12-26T15:10:29.875+05:30"
}
```

---

### 6. Get User Completion Status
**Endpoint:** `GET /api/progress/status/{userId}`

**Request:**
```bash
curl http://localhost:8086/api/progress/status/user-123
```

**Response:**
```json
{
  "userId": "user-123",
  "totalXp": 150,
  "tasksCompleted": 3,
  "tasksTotal": 4,
  "questsCompleted": 1,
  "questsTotal": 2,
  "badgesEarned": 1,
  "allTasksCompleted": false,
  "allQuestsCompleted": false,
  "isFullyCompleted": false
}
```

---

### 7. Get User Badges
**Endpoint:** `GET /api/badges/user/{userId}`

**Request:**
```bash
curl http://localhost:8086/api/badges/user/user-123
```

**Response:**
```json
[
  {
    "id": "badge-1",
    "name": "First Step",
    "description": "Complete your first task",
    "iconUrl": "/icons/first-step.png"
  }
]
```

---

## 🔐 Request/Response Details

### Common Query Parameters

| Parameter | Type | Description | Default |
|-----------|------|-------------|---------|
| `page` | integer | Page number (0-indexed) | 0 |
| `size` | integer | Items per page | 10 |
| `userId` | string | User ID (query param in progress endpoints) | required |

### Common Path Variables

| Variable | Type | Description |
|----------|------|-------------|
| `id` | string | Entity ID (user, topic, quest, task, badge) |
| `topicId` | string | Topic ID |
| `questId` | string | Quest ID |
| `taskId` | string | Task ID |
| `userId` | string | User ID |

### Standard Response Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Successful request |
| 201 | Created | Resource successfully created |
| 204 | No Content | Successful deletion |
| 400 | Bad Request | Invalid request parameters |
| 404 | Not Found | Resource not found |
| 500 | Server Error | Internal server error |

---

## 📊 Data Models

### User
```json
{
  "id": "string (UUID)",
  "name": "string",
  "email": "string",
  "totalXp": "integer"
}
```

### Topic
```json
{
  "id": "string",
  "name": "string",
  "description": "string"
}
```

### Quest
```json
{
  "id": "string",
  "topicId": "string",
  "name": "string",
  "description": "string",
  "orderIndex": "integer"
}
```

### Task
```json
{
  "id": "string",
  "questId": "string",
  "title": "string",
  "description": "string",
  "xpReward": "integer",
  "orderIndex": "integer"
}
```

### Badge
```json
{
  "id": "string",
  "name": "string",
  "description": "string",
  "iconUrl": "string"
}
```

### UserTaskProgress
```json
{
  "id": "string (UUID)",
  "userId": "string",
  "taskId": "string",
  "status": "NOT_STARTED|IN_PROGRESS|COMPLETED",
  "gainedXp": "integer",
  "updatedAt": "ISO 8601 timestamp"
}
```

### UserQuestProgress
```json
{
  "id": "string (UUID)",
  "userId": "string",
  "questId": "string",
  "status": "IN_PROGRESS|COMPLETED",
  "gainedXp": "integer"
}
```

---

## 🎯 Complete User Flow Example

### Step 1: Create User
```bash
curl -X POST http://localhost:8086/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane","email":"jane@example.com","totalXp":0}'
```
Response: `user-id: "123abc"`

### Step 2: Get Topics
```bash
curl http://localhost:8086/api/topics
```
Response: Topics list including `topic-java-101`

### Step 3: Get Topic Tree
```bash
curl http://localhost:8086/api/topics/topic-java-101/tree
```
Response: Topic with quests and tasks (e.g., `task-1`)

### Step 4: Start First Task
```bash
curl -X POST "http://localhost:8086/api/progress/start-task/task-1?userId=123abc"
```
Response: Task progress with `IN_PROGRESS` status

### Step 5: Complete Task
```bash
curl -X POST "http://localhost:8086/api/progress/complete-task/task-1?userId=123abc"
```
Response: Task progress with `COMPLETED` status, XP awarded

### Step 6: Check Status
```bash
curl http://localhost:8086/api/progress/status/123abc
```
Response: User stats (1 task completed, 50 XP earned, badges earned)

### Step 7: View Badges
```bash
curl http://localhost:8086/api/badges/user/123abc
```
Response: User's earned badges (First Step badge awarded)

---

## 🧪 Testing with Swagger UI

1. **Open Swagger UI**: http://localhost:8086/swagger-ui.html
2. **Expand a section** (e.g., "Progress")
3. **Click "Try it out"** button
4. **Fill in parameters** and **Execute**
5. **View response** with status code and body

### Interactive Features in Swagger UI
- ✅ Request body validation
- ✅ Parameter suggestions
- ✅ Live response examples
- ✅ Response schema display
- ✅ Error message details
- ✅ Download cURL commands

---

## 🔍 Common API Patterns

### Retrieve by ID
```bash
GET /api/{resource}/{id}
```
Returns single item or 404

### List All
```bash
GET /api/{resource}
```
Returns array of items (may be paginated)

### Create
```bash
POST /api/{resource}
Content-Type: application/json

{request body}
```
Returns created item or validation error

### Update
```bash
PUT /api/{resource}/{id}
Content-Type: application/json

{request body}
```
Returns updated item or 404

### Delete
```bash
DELETE /api/{resource}/{id}
```
Returns 204 No Content on success

---

## 📝 Error Response Format

### Standard Error Response
```json
{
  "message": "Error description"
}
```

### Examples

**Missing User:**
```bash
curl http://localhost:8086/api/users/nonexistent
```
Response: 404
```json
{
  "message": "User not found"
}
```

**Invalid Task:**
```bash
curl -X POST "http://localhost:8086/api/progress/start-task/invalid?userId=123"
```
Response: 400
```json
{
  "message": "Task not found"
}
```

---

## 🚀 Integration Tips for UI

### Frontend Setup

1. **Use generated client** (optional):
   - Tools like OpenAPI Generator can create TypeScript/JavaScript clients
   - Generates type-safe API calls

2. **Manual integration**:
   ```javascript
   const API_BASE = 'http://localhost:8086/api';
   
   async function startTask(taskId, userId) {
     const response = await fetch(
       `${API_BASE}/progress/start-task/${taskId}?userId=${userId}`,
       { method: 'POST' }
     );
     return response.json();
   }
   ```

3. **Use Swagger UI for testing**:
   - Before implementing in frontend
   - Test all endpoints
   - Understand response structure
   - Check error handling

### Handling Responses
- Check HTTP status code
- Parse JSON response body
- Handle 404s for missing resources
- Handle 400s for validation errors
- Display error messages to users

---

## 📦 Deployment Notes

### For Production

1. **Update server URL in OpenAPIConfig.java**:
   ```java
   new Server()
       .url("https://api.yourdomain.com")
       .description("Production Server")
   ```

2. **Disable Swagger UI in production** (optional):
   ```properties
   springdoc.swagger-ui.enabled=false
   ```

3. **Secure API endpoints**:
   - Add authentication
   - Add authorization checks
   - Rate limiting
   - CORS configuration update

---

## 🎓 Learning Resources

- **OpenAPI Specification**: https://spec.openapis.org/
- **Springdoc OpenAPI**: https://springdoc.org/
- **Swagger UI**: https://swagger.io/tools/swagger-ui/
- **API Design Best Practices**: https://restfulapi.net/

---

## 💡 Tips for UI Team

1. **Start with Swagger UI** - Use it to understand all endpoints before coding
2. **Test with cURL first** - Verify API behavior before frontend integration
3. **Keep base URL configurable** - Easy switching between dev/prod
4. **Implement error handling** - Show meaningful errors to users
5. **Use loading states** - Async operations take time
6. **Cache responses** - Reduce API calls
7. **Handle network errors** - Graceful degradation

---

**Last Updated**: December 26, 2025
**Status**: ✅ Complete with all endpoints documented
**Accessible At**: http://localhost:8086/swagger-ui.html

