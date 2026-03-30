# Food Delivery Application - Build and Deployment Guide

## Prerequisites

### System Requirements
- **Java**: JDK 17 or higher
- **Maven**: 3.6 or higher
- **MySQL**: 8.0 or higher
- **Apache Kafka**: Latest version
- **Git**: For version control

### Installation Verification

```bash
# Verify Java installation
java -version

# Verify Maven installation
mvn --version

# Verify MySQL installation
mysql --version
```

---

## Step 1: Database Setup

### Create Databases

```sql
-- Connect to MySQL
mysql -u root -p

-- Create all required databases
CREATE DATABASE user_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE restaurant_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE order_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE payment_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE delivery_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE notification_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create application user (optional but recommended)
CREATE USER 'fooddelivery'@'localhost' IDENTIFIED BY 'fooddelivery123';
GRANT ALL PRIVILEGES ON *.* TO 'fooddelivery'@'localhost';
FLUSH PRIVILEGES;

-- Verify databases
SHOW DATABASES;
```

---

## Step 2: Infrastructure Setup

### Start MySQL Server

**Windows:**
```bash
# If installed as service
net start MySQL80

# Or start manually
mysqld
```

**macOS:**
```bash
# Using Homebrew
brew services start mysql

# Or manually
mysql.server start
```

**Linux:**
```bash
sudo systemctl start mysql
```

### Start Apache Kafka

**Download Kafka** (if not already installed):
```bash
# Download from https://kafka.apache.org/downloads
# Extract to a location, e.g., /opt/kafka or C:\kafka
```

**Start Zookeeper:**
```bash
# Windows
bin\windows\zookeeper-server-start.bat config\zookeeper.properties

# macOS/Linux
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**Start Kafka Broker** (in a new terminal):
```bash
# Windows
bin\windows\kafka-server-start.bat config\server.properties

# macOS/Linux
bin/kafka-server-start.sh config/server.properties
```

**Verify Kafka is running:**
```bash
# Create a test topic
kafka-topics --create --topic test --bootstrap-server localhost:9092

# List topics
kafka-topics --list --bootstrap-server localhost:9092
```

### Start Eureka Server (Optional but Recommended)

Create a new Spring Boot project with Eureka Server:

```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

**application.yml:**
```yaml
server:
  port: 8761

eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
  server:
    enableSelfPreservation: false

spring:
  application:
    name: eureka-server
```

Run: `mvn spring-boot:run`

---

## Step 3: Build the Application

### Navigate to Project Root

```bash
cd "D:\Food Delivary App\FoodDeliveryApp"
```

### Clean Build

```bash
# Full clean build
mvn clean install

# Skip tests for faster build
mvn clean install -DskipTests

# Build specific module
mvn clean install -pl user-service
```

### Build Output

Successful build output:
```
[INFO] BUILD SUCCESS
[INFO] Total time: XX.XXX s
[INFO] Finished at: YYYY-MM-DD HH:MM:SS
```

---

## Step 4: Start Microservices

### Recommended Startup Order

Start each service in a separate terminal window:

#### Terminal 1: User Service
```bash
cd user-service
mvn spring-boot:run
# Or
java -jar target/user-service-1.0.0.jar
```
**Expected Output:** `Started UserServiceApplication in X.XXX seconds`
**Access:** http://localhost:8081/swagger-ui.html

#### Terminal 2: Restaurant Service
```bash
cd restaurant-service
mvn spring-boot:run
```
**Expected Output:** `Started RestaurantServiceApplication in X.XXX seconds`
**Access:** http://localhost:8082/swagger-ui.html

#### Terminal 3: Order Service
```bash
cd order-service
mvn spring-boot:run
```
**Expected Output:** `Started OrderServiceApplication in X.XXX seconds`
**Access:** http://localhost:8083/swagger-ui.html

#### Terminal 4: Payment Service
```bash
cd payment-service
mvn spring-boot:run
```
**Expected Output:** `Started PaymentServiceApplication in X.XXX seconds`
**Access:** http://localhost:8084/swagger-ui.html

#### Terminal 5: Delivery Service
```bash
cd delivery-service
mvn spring-boot:run
```
**Expected Output:** `Started DeliveryServiceApplication in X.XXX seconds`
**Access:** http://localhost:8086/swagger-ui.html

#### Terminal 6: Notification Service
```bash
cd notification-service
mvn spring-boot:run
```
**Expected Output:** `Started NotificationServiceApplication in X.XXX seconds`
**Access:** http://localhost:8085/swagger-ui.html

#### Terminal 7: API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```
**Expected Output:** `Started ApiGatewayApplication in X.XXX seconds`
**Access:** http://localhost:8080/swagger-ui.html

---

## Step 5: Verify Services

### Check Service Registration

```bash
# Eureka Dashboard
curl http://localhost:8761

# Expected: Eureka Server page showing all registered services
```

### Check Service Health

```bash
# User Service
curl http://localhost:8081/actuator/health

# Restaurant Service
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8083/actuator/health

# Payment Service
curl http://localhost:8084/actuator/health

# Delivery Service
curl http://localhost:8086/actuator/health

# Notification Service
curl http://localhost:8085/actuator/health

# API Gateway
curl http://localhost:8080/actuator/health
```

### Check Swagger UI

- User Service: http://localhost:8081/swagger-ui.html
- Restaurant Service: http://localhost:8082/swagger-ui.html
- Order Service: http://localhost:8083/swagger-ui.html
- Payment Service: http://localhost:8084/swagger-ui.html
- Delivery Service: http://localhost:8086/swagger-ui.html
- Notification Service: http://localhost:8085/swagger-ui.html
- API Gateway: http://localhost:8080/swagger-ui.html

---

## Step 6: Test the Application

### Register a User

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

### Login to Get JWT Token

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer",
  "expiresIn": 86400
}
```

### Use Token in Requests

```bash
# Store token in variable
TOKEN="your_jwt_token_here"

# Get user profile
curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Troubleshooting

### Port Already in Use

```bash
# Windows: Find process using port
netstat -ano | findstr :8080

# Kill process
taskkill /PID <PID> /F

# macOS/Linux: Find process using port
lsof -i :8080

# Kill process
kill -9 <PID>
```

### Database Connection Error

```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1"

# Check database exists
mysql -u root -p -e "SHOW DATABASES;"

# Check user permissions
mysql -u root -p -e "SHOW GRANTS FOR 'fooddelivery'@'localhost';"
```

### Kafka Connection Error

```bash
# Verify Kafka is running
jps -l | grep Kafka

# Check Kafka logs
tail -f logs/server.log

# Verify broker is listening
netstat -an | grep 9092
```

### Service Not Registering with Eureka

```bash
# Check Eureka logs for errors
# Verify eureka.client.service-url.defaultZone in application.yml
# Ensure Eureka Server is running on port 8761
```

### JWT Token Errors

```bash
# Verify JWT secret is configured in application.yml
# Ensure token format: "Authorization: Bearer <token>"
# Check token expiration time
```

---

## Production Deployment

### Docker Deployment

Create `Dockerfile` for each service:

```dockerfile
FROM openjdk:17-slim
COPY target/service-name-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:

```bash
# Build Docker image
docker build -t food-delivery/user-service .

# Run Docker container
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/user_db \
  -e SPRING_DATASOURCE_USERNAME=fooddelivery \
  -e SPRING_DATASOURCE_PASSWORD=fooddelivery123 \
  food-delivery/user-service
```

### Docker Compose

Create `docker-compose.yml`:

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql

  kafka:
    image: confluentinc/cp-kafka:latest
    environment:
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
    ports:
      - "9092:9092"

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
    ports:
      - "2181:2181"

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    depends_on:
      - mysql
      - kafka

  # Add other services similarly

volumes:
  mysql_data:
```

Run: `docker-compose up -d`

### Kubernetes Deployment

Create deployment manifests for each service with:
- Deployment
- Service
- ConfigMap
- Secrets

---

## Performance Tuning

### JVM Heap Size

```bash
# Set heap size when starting service
java -Xmx1024m -Xms512m -jar user-service-1.0.0.jar
```

### Database Connection Pooling

Configured in `application.yml`:
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 20000
```

### Kafka Optimization

```yaml
spring:
  kafka:
    producer:
      batch-size: 16384
      linger-ms: 10
    consumer:
      max-poll-records: 500
```

---

## Monitoring and Logging

### Application Logs

Logs are written to console by default. Configure file logging in `application.yml`:

```yaml
logging:
  file:
    name: logs/service-name.log
  level:
    root: INFO
    com.fooddelivery: DEBUG
```

### Metrics

Access metrics at: `http://localhost:PORT/actuator/metrics`

### Health Check

Access health at: `http://localhost:PORT/actuator/health`

---

## Maintenance

### Database Backup

```bash
# Backup all databases
mysqldump -u root -p --all-databases > backup.sql

# Restore from backup
mysql -u root -p < backup.sql
```

### Clean Up

```bash
# Stop all services
# Stop Kafka and Zookeeper
# Stop MySQL
# Clean Maven cache
mvn clean

# Remove Docker containers
docker-compose down
```

---

## Support and Documentation

- **API Documentation**: http://localhost:8080/swagger-ui.html
- **Eureka Dashboard**: http://localhost:8761
- **Module Documentation**: See MODULES_SUMMARY.md
- **Setup Guide**: See SETUP_GUIDE.md
- **Compilation Fixes**: See COMPILATION_FIXES.md

---

**Last Updated**: March 30, 2026
**Status**: Ready for Production
