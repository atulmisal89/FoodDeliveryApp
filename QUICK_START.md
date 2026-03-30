# Food Delivery Application - Quick Start Guide

## 5-Minute Setup

### Prerequisites Check
```bash
java -version          # Should be 17+
mvn --version         # Should be 3.6+
mysql --version       # Should be 8.0+
```

### 1. Database Setup (2 minutes)
```sql
mysql -u root -p
CREATE DATABASE user_db;
CREATE DATABASE restaurant_db;
CREATE DATABASE order_db;
CREATE DATABASE payment_db;
CREATE DATABASE delivery_db;
CREATE DATABASE notification_db;
EXIT;
```

### 2. Start Infrastructure (2 minutes)

**Terminal 1 - Zookeeper:**
```bash
cd kafka
bin/zookeeper-server-start.sh config/zookeeper.properties
```

**Terminal 2 - Kafka:**
```bash
cd kafka
bin/kafka-server-start.sh config/server.properties
```

**Terminal 3 - Eureka Server (Optional):**
```bash
# Create and run Eureka server on port 8761
# Or skip if not using service discovery
```

### 3. Build Application (3 minutes)
```bash
cd "D:\Food Delivary App\FoodDeliveryApp"
mvn clean install -DskipTests
```

### 4. Start Services (1 minute each)

Open 7 terminals and run in each:

```bash
# Terminal 1
cd user-service && mvn spring-boot:run

# Terminal 2
cd restaurant-service && mvn spring-boot:run

# Terminal 3
cd order-service && mvn spring-boot:run

# Terminal 4
cd payment-service && mvn spring-boot:run

# Terminal 5
cd delivery-service && mvn spring-boot:run

# Terminal 6
cd notification-service && mvn spring-boot:run

# Terminal 7
cd api-gateway && mvn spring-boot:run
```

### 5. Verify Services
```bash
# Check API Gateway
curl http://localhost:8080/swagger-ui.html

# Check Eureka
curl http://localhost:8761
```

---

## Quick API Test

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "test123"
  }'
```

### Use Token
```bash
# Replace TOKEN with actual JWT from login response
TOKEN="your_jwt_token"

curl -X GET http://localhost:8080/api/users/1 \
  -H "Authorization: Bearer $TOKEN"
```

---

## Service URLs

| Service | Port | Swagger |
|---------|------|---------|
| API Gateway | 8080 | http://localhost:8080/swagger-ui.html |
| User Service | 8081 | http://localhost:8081/swagger-ui.html |
| Restaurant Service | 8082 | http://localhost:8082/swagger-ui.html |
| Order Service | 8083 | http://localhost:8083/swagger-ui.html |
| Payment Service | 8084 | http://localhost:8084/swagger-ui.html |
| Notification Service | 8085 | http://localhost:8085/swagger-ui.html |
| Delivery Service | 8086 | http://localhost:8086/swagger-ui.html |
| Eureka Server | 8761 | http://localhost:8761 |

---

## Troubleshooting

### Port Already in Use
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux
lsof -i :8080
kill -9 <PID>
```

### MySQL Connection Failed
```bash
# Verify MySQL is running
mysql -u root -p -e "SELECT 1"

# Check databases exist
mysql -u root -p -e "SHOW DATABASES;"
```

### Kafka Connection Failed
```bash
# Verify Kafka is running
jps -l | grep Kafka

# Check broker is listening
netstat -an | grep 9092
```

### Service Won't Start
```bash
# Check logs for errors
# Verify application.yml configuration
# Ensure all databases are created
# Check port is not in use
```

---

## Next Steps

1. Read `MODULES_SUMMARY.md` for detailed module information
2. Read `SETUP_GUIDE.md` for comprehensive setup instructions
3. Read `BUILD_AND_DEPLOY.md` for production deployment
4. Check `COMPILATION_FIXES.md` for any build issues

---

**Ready to go!** 🚀
