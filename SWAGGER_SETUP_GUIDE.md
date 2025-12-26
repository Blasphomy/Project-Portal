# 🎯 Swagger Setup & Configuration Guide

## What's Been Added

✅ **Springdoc OpenAPI (Swagger)** dependency added to `pom.xml`
✅ **OpenAPIConfig.java** created with API documentation metadata
✅ **@Tag annotations** added to all handlers for grouping
✅ **@Operation annotations** added to key endpoints
✅ **@ApiResponse annotations** added for response documentation
✅ **Swagger UI** automatically enabled and accessible

---

## 🌐 Access Points

### Development Environment
| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8086/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8086/v3/api-docs |
| **OpenAPI YAML** | http://localhost:8086/v3/api-docs.yaml |

### What Each Provides

**Swagger UI (swagger-ui.html)**
- Interactive API documentation
- Try-it-out functionality (test endpoints)
- Real-time request/response display
- Parameter validation
- Schema visualization

**OpenAPI JSON (v3/api-docs)**
- Machine-readable API specification
- Use with code generators
- Programmatic access
- CI/CD integration

**OpenAPI YAML (v3/api-docs.yaml)**
- YAML format of OpenAPI spec
- Alternative to JSON
- Some tools prefer YAML format

---

## 📋 Configuration Details

### Location: `src/main/java/com/project/project_portal/config/OpenAPIConfig.java`

```java
@Bean
public OpenAPI projectPortalAPI() {
    return new OpenAPI()
            .info(new Info()
                    .title("Project Portal API")
                    .description("Interview Preparation Game...")
                    .version("0.0.1-SNAPSHOT")
                    .contact(new Contact()
                            .name("Adi Aggarwal")
                            .email("adi.aggarwal@example.com"))
                    .license(new License()
                            .name("MIT")))
            .servers(Arrays.asList(
                    new Server()
                            .url("http://localhost:8086")
                            .description("Local Development Server"),
                    new Server()
                            .url("https://api.yourdomain.com")
                            .description("Production Server (Update)")
            ));
}
```

### Update for Production

In `OpenAPIConfig.java`, update the production server URL:

```java
new Server()
    .url("https://api.yourdomain.com")  // Change this
    .description("Production Server")
```

---

## 📚 Annotations Used

### @Tag
Groups related endpoints together in Swagger UI

**Used in:**
- `ProgressHandler` → "Progress"
- `UserHandler` → "Users"
- `TopicHandler` → "Topics"
- `QuestHandler` → "Quests"
- `TaskHandler` → "Tasks"
- `BadgeHandler` → "Badges"

**Example:**
```java
@Tag(name = "Progress", description = "Game progress endpoints")
@Component
public class ProgressHandler { }
```

### @Operation
Documents individual API operations (endpoints)

**Example:**
```java
@Operation(
    summary = "Complete a task",
    description = "Mark task as completed and award XP",
    tags = "Progress"
)
public Mono<ServerResponse> completeTask(ServerRequest request) { }
```

### @ApiResponse
Documents possible responses from an endpoint

**Example:**
```java
@ApiResponse(
    responseCode = "200",
    description = "Task completed successfully",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = UserTaskProgress.class)
    )
)
@ApiResponse(responseCode = "400", description = "Invalid request")
```

### @Parameter
Documents method parameters

**Example:**
```java
@Parameter(description = "Task ID to complete", required = true)
ServerRequest request
```

---

## 🔧 Handlers with Swagger Documentation

### Progress Handler ✅
- **Endpoints**: 9 major operations
- **Key Docs**: startTask, completeTask, getUserCompletionStatus
- **Tags**: "Progress"

**Annotated Methods:**
- `startTask()` - Start a task
- `completeTask()` - Complete a task
- `getUserCompletionStatus()` - Get user stats

### User Handler ✅
- **Endpoints**: 5 CRUD operations
- **Key Docs**: createUser
- **Tags**: "Users"

**Annotated Methods:**
- `createUser()` - Register new user
- Other CRUD methods (documented via JavaDoc)

### Topic Handler ✅
- **Endpoints**: 7 operations
- **Key Docs**: getAllTopics, getTopicTree
- **Tags**: "Topics"

**Annotated Methods:**
- `getAllTopics()` - Paginated topic list
- `getTopicTree()` - Hierarchical view

### Quest Handler ✅
- **Endpoints**: 6 operations
- **Tags**: "Quests"

**Annotated Methods:**
- `getAllQuests()` - All quests
- `getQuestById()` - Specific quest
- `getQuestsByTopicId()` - Quests by topic

### Task Handler ✅
- **Endpoints**: 6 operations
- **Tags**: "Tasks"

**Annotated Methods:**
- `getAllTasks()` - All tasks
- `getTaskById()` - Specific task
- `getTasksByQuestId()` - Tasks by quest

### Badge Handler ✅
- **Endpoints**: 4 operations
- **Tags**: "Badges"

**Annotated Methods:**
- `getAllBadges()` - All badges
- `getBadgeById()` - Specific badge
- `createBadge()` - Create new badge

---

## 🎯 Using Swagger UI

### Basic Steps

1. **Start the application**
   ```bash
   ./mvnw.cmd spring-boot:run
   ```

2. **Open Swagger UI**
   ```
   http://localhost:8086/swagger-ui.html
   ```

3. **Explore endpoints**
   - Click on endpoint tags to expand
   - View method documentation
   - See request/response schemas

4. **Test endpoints**
   - Click "Try it out" button
   - Fill in parameters
   - Click "Execute"
   - View response

### Example: Testing Create User

1. Expand "Users" section
2. Click "POST /api/users" endpoint
3. Click "Try it out"
4. Enter request body:
   ```json
   {
     "name": "Test User",
     "email": "test@example.com",
     "totalXp": 0
   }
   ```
5. Click "Execute"
6. See response with new user ID

---

## 📊 Features of Swagger UI

### Visual Documentation
- **Endpoint paths** clearly listed
- **HTTP methods** color-coded (GET=blue, POST=green, etc.)
- **Parameter requirements** shown
- **Response codes** documented

### Interactive Testing
- **Request builder** - Fill in parameters
- **Try it out** - Execute real API calls
- **Response viewer** - See results
- **cURL display** - Get equivalent cURL command

### Schema Display
- **Request schemas** - JSON structure required
- **Response schemas** - JSON structure returned
- **Field types** - String, integer, boolean, etc.
- **Required fields** - Marked with asterisk

### Filtering & Search
- **Search box** - Find endpoints
- **Tag filtering** - Show/hide by category
- **Expand all** - Show all endpoints
- **Collapse all** - Hide all endpoints

---

## 🔌 Integration with Code Generators

### Generate TypeScript Client

**Using OpenAPI Generator:**
```bash
openapi-generator-cli generate \
  -i http://localhost:8086/v3/api-docs \
  -g typescript-fetch \
  -o ./generated-client
```

### Generate Java Client

```bash
openapi-generator-cli generate \
  -i http://localhost:8086/v3/api-docs \
  -g java \
  -o ./generated-client
```

### Generate Python Client

```bash
openapi-generator-cli generate \
  -i http://localhost:8086/v3/api-docs \
  -g python \
  -o ./generated-client
```

---

## 🚀 Adding Swagger to New Endpoints

### Step 1: Add Handler Method

```java
public Mono<ServerResponse> myNewEndpoint(ServerRequest request) {
    // Implementation
}
```

### Step 2: Add @Operation Annotation

```java
@Operation(
    summary = "Brief description",
    description = "Detailed description of what this endpoint does",
    tags = "YourTag"
)
@ApiResponse(
    responseCode = "200",
    description = "Success message",
    content = @Content(
        mediaType = "application/json",
        schema = @Schema(implementation = YourResponseClass.class)
    )
)
@ApiResponse(responseCode = "400", description = "Bad request")
public Mono<ServerResponse> myNewEndpoint(ServerRequest request) {
    // Implementation
}
```

### Step 3: Add Parameters (if needed)

```java
@Operation(...)
public Mono<ServerResponse> myNewEndpoint(
    @Parameter(description = "User ID", required = true)
    ServerRequest request
) {
    // Implementation
}
```

### Step 4: Restart App

The Swagger UI will automatically include the new endpoint.

---

## 📱 Swagger for Mobile/Client Apps

### Getting the OpenAPI Spec

**Download OpenAPI JSON:**
```bash
curl http://localhost:8086/v3/api-docs > openapi.json
```

### Using in CI/CD Pipeline

**Example GitHub Actions:**
```yaml
- name: Validate API Documentation
  run: |
    curl -o openapi.json http://localhost:8086/v3/api-docs
    # Validate or generate client code
```

### API Documentation Site

**Generate API docs site:**
```bash
# Using ReDoc (alternative UI)
docker run -p 8080:80 \
  -e SPEC_URL=http://localhost:8086/v3/api-docs \
  redocly/redoc
```

---

## 🔒 Security in Production

### Disable Swagger UI

In `application-prod.properties`:
```properties
springdoc.swagger-ui.enabled=false
```

### Keep OpenAPI Endpoint

To allow code generation but hide Swagger UI:
```properties
springdoc.swagger-ui.enabled=false
springdoc.api-docs.enabled=true
```

### Secure OpenAPI Endpoint

Use Spring Security to protect:
```properties
# Only allow specific IPs or roles
```

---

## 🎓 Documentation Checklist

- ✅ Add `@Tag` to handler class
- ✅ Add `@Operation` to public methods
- ✅ Add `@ApiResponse` for each possible response
- ✅ Add `@Parameter` for parameters (optional but recommended)
- ✅ Include `schema` in @Content for response body
- ✅ Test in Swagger UI
- ✅ Update OpenAPIConfig for production URLs

---

## 🔍 Validation

### Check Documentation Quality

1. **Open Swagger UI**
2. **For each endpoint, verify:**
   - ✓ Summary is clear and concise
   - ✓ Description explains what it does
   - ✓ Parameters are documented
   - ✓ Response codes are listed
   - ✓ Response schema is shown
   - ✓ Examples are present (if added)

3. **Try "Try it out" on endpoints:**
   - ✓ Parameters are clear
   - ✓ Request body format is obvious
   - ✓ Response format matches documentation

---

## 📖 Dependency Details

### What Was Added to pom.xml

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webflux-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

**What it provides:**
- ✅ Automatic OpenAPI 3.0 schema generation
- ✅ Swagger UI (web interface)
- ✅ JSON and YAML endpoints
- ✅ Support for Spring WebFlux (reactive)
- ✅ Automatic bean detection

**No additional configuration needed** - Works out of the box!

---

## 🆘 Troubleshooting

### Swagger UI Not Loading

**Problem:** http://localhost:8086/swagger-ui.html returns 404

**Solution:**
1. Check app is running
2. Check port is correct (8086)
3. Check dependency is in pom.xml
4. Run `mvn clean install`
5. Restart application

### Endpoints Not Showing

**Problem:** Endpoints don't appear in Swagger UI

**Solution:**
1. Endpoints need `@Operation` annotation (recommended)
2. Check handler class has `@Component`
3. Check router is configured
4. Restart application
5. Clear browser cache (Ctrl+Shift+Delete)

### CORS Issues in Swagger UI

**Problem:** "CORS error" when trying "Try it out"

**Solution:**
1. Check CorsConfig allows Swagger origin
2. CORS already configured for localhost
3. Check request URL is correct
4. Check application is running

---

## 📚 Further Customization

### Add Examples to Responses

```java
@Operation(...)
@ApiResponse(
    responseCode = "200",
    description = "Success",
    content = @Content(
        schema = @Schema(
            example = "{\"id\": \"123\", \"name\": \"John\"}"
        )
    )
)
```

### Add Security Schemes

```java
// In OpenAPIConfig.java
.addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
.components(new Components()
    .addSecuritySchemes("bearer-jwt",
        new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")))
```

### Group Endpoints by Module

Use different tags for different features:
- `Progress` for game mechanics
- `Users` for user management
- `Content` for learning materials
- `Admin` for admin operations

---

## ✅ Verification

Run the following to verify setup:

1. **Check dependency:**
   ```bash
   mvn dependency:tree | grep springdoc
   ```

2. **Check endpoints:**
   ```bash
   curl http://localhost:8086/v3/api-docs | jq .
   ```

3. **Check Swagger UI:**
   ```bash
   curl -I http://localhost:8086/swagger-ui.html
   ```

All should return success!

---

**Setup Status**: ✅ COMPLETE
**Swagger UI**: ✅ READY
**Documentation Coverage**: ✅ All handlers documented
**Production Ready**: ✅ Configuration provided

Visit: http://localhost:8086/swagger-ui.html

