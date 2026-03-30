# Food Delivery Application - Setup Guide

## Complete Setup Instructions

### Step 1: Prerequisites Installation

#### Java 17
- Download from: https://www.oracle.com/java/technologies/downloads/#java17
- Verify installation: `java -version`

#### MySQL 8.0+
- Download from: https://dev.mysql.com/downloads/mysql/
- Start MySQL Server
- Verify: `mysql --version`

#### Apache Kafka
- Download from: https://kafka.apache.org/downloads
- Extract and navigate to kafka directory
- Start Zookeeper: `bin/zookeeper-server-start.sh config/zookeeper.properties`
- Start Kafka: `bin/kafka-server-start.sh config/server.properties`

#### Maven 3.6+
- Download from: https://maven.apache.org/download.cgi
- Verify: `mvn --version`

#### Eureka Server (Optional - for service discovery)
- Create a separate Spring Boot project with `@EnableEurekaServer`
- Or use a pre-built Eureka server image

### Step 2: Database Setup

Open MySQL and execute:

```sql
-- Create all databases
CREATE DATABASE user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE payment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user for the application (optional but recommended)
CREATE USER 'fooddelivery'@'localhost' IDENTIFIED BY 'fooddelivery123';
GRANT ALL PRIVILEGES ON *.* TO 'fooddelivery'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3: Project Build

Navigate to the project root directory and run:

```bash
# Clean and build all modules
mvn clean install -DskipTests

# Or with tests
mvn clean install
```

This will:
- Download all dependencies
- Compile all modules
- Run tests (if not skipped)
- Create JAR files for each service

### Step 4: Configuration Updates

Before running services, update the following files with your environment settings:

#### For Email Notifications (notification-service)
Edit: `notification-service/src/main/resources/application.yml`

```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password  # Use Gmail App Password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true
            required: true
```

#### For Database Credentials (if different)
Edit each service's `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/database_name
    username: root
    password: root
```

#### For JWT Secret (Security)
Edit each service's `application.yml`:

```yaml
jwt:
  secret: your-very-long-secret-key-at-least-32-characters-long
  expiration: 86400000  # 24 hours in milliseconds
```

### Step 5: Start Services

Open separate terminal windows and start services in this order:

#### Terminal 1: Eureka Server (if using)
```bash
# Navigate to Eureka server directory
mvn spring-boot:run
# Should be available at http://localhost:8761
```

#### Terminal 2: User Service
```bash
cd FoodDeliveryApp
mvn spring-boot:run -pl user-service
# Runs on http://localhost:8081
```

#### Terminal 3: Restaurant Service
```bash
mvn spring-boot:run -pl restaurant-service
# Runs on http://localhost:8082
```

#### Terminal 4: Order Service
```bash
mvn spring-boot:run -pl order-service
# Runs on http://localhost:8083
```

#### Terminal 5: Payment Service
```bash
mvn spring-boot:run -pl payment-service
# Runs on http://localhost:8084
```

#### Terminal 6: Notification Service
```bash
mvn spring-boot:run -pl notification-service
# Runs on http://localhost:8085
```

#### Terminal 7: Delivery Service
```bash
mvn spring-boot:run -pl delivery-service
# Runs on http://localhost:8086
```

#### Terminal 8: API Gateway
```bash
mvn spring-boot:run -pl api-gateway
# Runs on http://localhost:8080
```

### Step 6: Verify Services

Check if all services are running:

```bash
# Check Eureka Dashboard
curl http://localhost:8761

# Check API Gateway
curl http://localhost:8080/swagger-ui.html

# Check individual services
curl http://localhost:8081/swagger-ui.html  # User Service
curl http://localhost:8082/swagger-ui.html  # Restaurant Service
curl http://localhost:8083/swagger-ui.html  # Order Service
curl http://localhost:8084/swagger-ui.html  # Payment Service
curl http://localhost:8085/swagger-ui.html  # Notification Service
curl http://localhost:8086/swagger-ui.html  # Delivery Service
```

## Testing the Application

### 1. Register a User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe"
  }'
```

### 2. Login to Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

Response will include a JWT token. Copy it.

### 3. Use Token in Requests

```bash
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 4. Create a Restaurant

```bash
curl -X POST http://localhost:8080/api/restaurants \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "Pizza Palace",
    "address": "123 Main St",
    "phone": "555-1234",
    "cuisineType": "Italian"
  }'
```

### 5. Create an Order

```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "customerId": 1,
    "restaurantId": 1,
    "restaurantName": "Pizza Palace",
    "deliveryAddress": "456 Oak Ave",
    "paymentMethod": "CARD",
    "items": [
      {
        "menuItemId": 1,
        "itemName": "Margherita Pizza",
        "quantity": 2,
        "unitPrice": 12.99
      }
    ]
  }'
```

## Troubleshooting

### Issue: Services not registering with Eureka

**Solution**:
1. Verify Eureka Server is running on port 8761
2. Check network connectivity
3. Verify `eureka.client.service-url.defaultZone` in each service's application.yml

### Issue: Database connection refused

**Solution**:
1. Verify MySQL is running: `mysql -u root -p`
2. Check database credentials in application.yml
3. Verify databases are created: `SHOW DATABASES;`

### Issue: Kafka connection errors

**Solution**:
1. Verify Kafka is running on port 9092
2. Verify Zookeeper is running
3. Check Kafka logs for errors

### Issue: JWT authentication failures

**Solution**:
1. Verify token format: `Authorization: Bearer <token>`
2. Check token hasn't expired
3. Verify JWT secret is consistent across services

### Issue: Port already in use

**Solution**:
1. Find process using port: `lsof -i :8080` (macOS/Linux) or `netstat -ano | findstr :8080` (Windows)
2. Kill process: `kill -9 <PID>` (macOS/Linux) or `taskkill /PID <PID> /F` (Windows)
3. Or change port in application.yml

### Issue: OutOfMemory errors

**Solution**:
Increase heap size when starting services:
```bash
export MAVEN_OPTS="-Xmx1024m -Xms512m"
mvn spring-boot:run -pl service-name
```

## Performance Optimization

### Enable Caching
Add Redis dependency and configure caching in services that need it.

### Database Optimization
- Create indexes on frequently queried columns
- Use connection pooling (HikariCP is default)
- Monitor slow queries

### Kafka Optimization
- Adjust batch size and linger time
- Monitor consumer lag
- Partition topics appropriately

## Security Checklist

- [ ] Change default JWT secret to a strong value
- [ ] Change default database passwords
- [ ] Enable HTTPS/TLS for production
- [ ] Implement rate limiting on API Gateway
- [ ] Add API key authentication for service-to-service communication
- [ ] Implement request validation and sanitization
- [ ] Enable CORS only for trusted domains
- [ ] Use environment variables for sensitive configuration
- [ ] Implement audit logging
- [ ] Regular security updates for dependencies

## Monitoring and Logging

### View Logs
Each service logs to console. For file logging, add to application.yml:

```yaml
logging:
  file:
    name: logs/service-name.log
  level:
    root: INFO
    com.fooddelivery: DEBUG
```

### Monitor Services
- Eureka Dashboard: http://localhost:8761
- Swagger UI: http://localhost:8080/swagger-ui.html
- Check database: `SELECT * FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'database_name';`

## Deployment

### Docker Deployment
Create Dockerfile for each service:

```dockerfile
FROM openjdk:17-slim
COPY target/service-name-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t food-delivery/service-name .
docker run -p 8081:8081 food-delivery/service-name
```

### Kubernetes Deployment
Create deployment manifests for each service with appropriate resource limits and health checks.

## Additional Resources

- Spring Boot Documentation: https://spring.io/projects/spring-boot
- Spring Cloud Documentation: https://spring.io/projects/spring-cloud
- Kafka Documentation: https://kafka.apache.org/documentation/
- MySQL Documentation: https://dev.mysql.com/doc/
- JWT Documentation: https://jwt.io/

## Support

For detailed module information, refer to `MODULES_SUMMARY.md`.
For API documentation, visit the Swagger UI endpoints.
