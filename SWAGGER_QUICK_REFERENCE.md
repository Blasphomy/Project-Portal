# 🎯 Swagger Quick Reference Card

## 🌐 Access Points

```
🔗 Swagger UI (Interactive)
   http://localhost:8086/swagger-ui.html

📋 OpenAPI JSON (Machine-readable)
   http://localhost:8086/v3/api-docs

📄 OpenAPI YAML (Alternative format)
   http://localhost:8086/v3/api-docs.yaml
```

---

## 🚀 Getting Started

```bash
# 1. Start the application
./mvnw.cmd spring-boot:run

# 2. Open in browser
http://localhost:8086/swagger-ui.html

# 3. Explore endpoints and test with "Try it out"
```

---

## 📚 Documentation

| Document | Purpose | Time |
|----------|---------|------|
| SWAGGER_API_GUIDE.md | API reference & examples | 20 min |
| SWAGGER_SETUP_GUIDE.md | Setup & customization | 15 min |
| DOCUMENTATION_INDEX.md | Navigation | 5 min |

---

## 🎮 Endpoint Groups (37 Total)

### Progress (9)
```
POST   /api/progress/start-task/{taskId}
POST   /api/progress/complete-task/{taskId}
GET    /api/progress/status/{userId}
```

### Users (5)
```
GET    /api/users/
POST   /api/users/
GET    /api/users/{id}
PUT    /api/users/{id}
DELETE /api/users/{id}
```

### Topics (7)
```
GET    /api/topics/ (page, size)
GET    /api/topics/{id}
GET    /api/topics/{id}/tree (hierarchical)
POST   /api/topics/
```

### Quests (6)
```
GET    /api/quests/
GET    /api/quests/topic/{topicId}
POST   /api/quests/
```

### Tasks (6)
```
GET    /api/tasks/
GET    /api/tasks/quest/{questId}
POST   /api/tasks/
```

### Badges (4)
```
GET    /api/badges/
GET    /api/badges/user/{userId}
POST   /api/badges/
```

---

## 💡 Quick Examples

### Create User
```bash
curl -X POST http://localhost:8086/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John","email":"john@example.com","totalXp":0}'
```

### Get Topics
```bash
curl http://localhost:8086/api/topics?page=0&size=10
```

### Start Task
```bash
curl -X POST "http://localhost:8086/api/progress/start-task/task-1?userId=user-123"
```

### Complete Task
```bash
curl -X POST "http://localhost:8086/api/progress/complete-task/task-1?userId=user-123"
```

### Check Status
```bash
curl http://localhost:8086/api/progress/status/user-123
```

---

## ✨ Swagger UI Features

| Feature | How To |
|---------|--------|
| **Test Endpoint** | Click "Try it out" → Fill params → Execute |
| **View Schema** | Click endpoint → See "Schema" section |
| **Copy cURL** | Click endpoint → See cURL command at bottom |
| **See Examples** | Click endpoint → See "Request/Response" examples |
| **Filter** | Use search box or collapse/expand tags |

---

## 📊 Request/Response Pattern

### Pattern: GET all items
```bash
GET /api/{resource}
```
Returns: Array of items (may be paginated)

### Pattern: GET by ID
```bash
GET /api/{resource}/{id}
```
Returns: Single item or 404

### Pattern: POST create
```bash
POST /api/{resource}
Body: { JSON data }
```
Returns: Created item

### Pattern: PUT update
```bash
PUT /api/{resource}/{id}
Body: { JSON data }
```
Returns: Updated item or 404

### Pattern: DELETE
```bash
DELETE /api/{resource}/{id}
```
Returns: 204 No Content

---

## 🔄 Complete User Flow

```
1. Create User
   POST /api/users
   → Get: user-123

2. Get Topics
   GET /api/topics
   → Get: topic-java-101

3. Get Topic Tree
   GET /api/topics/topic-java-101/tree
   → Get: quests and tasks

4. Start Task
   POST /api/progress/start-task/task-1?userId=user-123
   → Status: IN_PROGRESS

5. Complete Task
   POST /api/progress/complete-task/task-1?userId=user-123
   → Status: COMPLETED, XP: 50

6. Check Status
   GET /api/progress/status/user-123
   → 1 task completed, 50 XP, 1 badge earned

7. View Badges
   GET /api/badges/user/user-123
   → badge-1 (First Step)
```

---

## 🎯 For Developers

### Frontend
- Use SWAGGER_API_GUIDE.md
- Test in Swagger UI before coding
- Generate TypeScript client (optional)

### Backend
- Use SWAGGER_SETUP_GUIDE.md to add docs to new endpoints
- Keep docs in sync with code
- Reference for troubleshooting

### QA/Testing
- Use Swagger UI for API testing
- Document test cases with endpoint references
- Use OpenAPI spec for automated testing

---

## ⚠️ Response Codes

| Code | Meaning |
|------|---------|
| 200 | Success ✅ |
| 201 | Created ✅ |
| 204 | No Content ✅ |
| 400 | Bad Request ❌ |
| 404 | Not Found ❌ |
| 500 | Server Error ❌ |

---

## 🔧 Configuration Files

**pom.xml**: Added springdoc-openapi-starter-webflux-ui dependency
**OpenAPIConfig.java**: API metadata, server URLs, titles
**Handlers (6 files)**: @Tag, @Operation, @ApiResponse annotations

---

## 📱 Code Generation

### Generate TypeScript Client
```bash
openapi-generator-cli generate \
  -i http://localhost:8086/v3/api-docs \
  -g typescript-fetch \
  -o ./generated-client
```

### Generate JavaScript Client
```bash
openapi-generator-cli generate \
  -i http://localhost:8086/v3/api-docs \
  -g javascript \
  -o ./generated-client
```

---

## ✅ Checklist

- [ ] Read SWAGGER_API_GUIDE.md
- [ ] Open Swagger UI (http://localhost:8086/swagger-ui.html)
- [ ] Explore all endpoint groups
- [ ] Test at least one endpoint with "Try it out"
- [ ] Copy cURL command from an endpoint
- [ ] Review request/response schemas
- [ ] Read complete user flow example
- [ ] Understand error handling
- [ ] Share Swagger UI link with team

---

## 🆘 Troubleshooting

**Swagger UI not loading?**
→ Check app is running on port 8086
→ Check dependency in pom.xml
→ Clear browser cache

**Endpoint not showing?**
→ Restart application
→ Check @Operation annotation is present
→ Check handler has @Component

**CORS error?**
→ CORS already configured for localhost
→ No additional configuration needed

---

## 📞 Questions?

| Question | Document |
|----------|----------|
| How do I use an API? | SWAGGER_API_GUIDE.md |
| How is it configured? | SWAGGER_SETUP_GUIDE.md |
| Where do I start? | DOCUMENTATION_INDEX.md |
| How do I add docs? | SWAGGER_SETUP_GUIDE.md |

---

## 🎉 You're Ready!

✅ Swagger is configured
✅ All endpoints documented
✅ Interactive testing available
✅ Examples provided
✅ Ready to build frontend

**Start Here**: http://localhost:8086/swagger-ui.html

---

**Quick Reference v1.0** | December 26, 2025

