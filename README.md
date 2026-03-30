# Food Delivery Application - Microservices Architecture

A complete microservices-based food delivery platform built with Spring Boot and Spring Cloud, featuring service discovery, API gateway, distributed messaging, and comprehensive business logic.

## Project Structure

```
FoodDeliveryApp/
├── user-service/              # User authentication and profile management
├── restaurant-service/        # Restaurant and menu management
├── order-service/             # Order creation and management
├── payment-service/           # Payment processing
├── delivery-service/          # Delivery tracking and management
├── notification-service/      # Email and SMS notifications
├── api-gateway/               # Central API gateway with JWT auth
├── pom.xml                    # Parent Maven configuration
└── MODULES_SUMMARY.md         # Detailed module documentation
```

## Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Apache Kafka
- Maven 3.6+
- Eureka Server (Spring Cloud Netflix Eureka)

### Installation Steps

1. **Clone and Navigate**
   ```bash
   cd FoodDeliveryApp
   ```

2. **Create Databases**
   ```sql
   CREATE DATABASE user_db;
   CREATE DATABASE restaurant_db;
   CREATE DATABASE order_db;
   CREATE DATABASE payment_db;
   CREATE DATABASE delivery_db;
   CREATE DATABASE notification_db;
   ```

3. **Start Infrastructure Services**
   - Start MySQL Server
   - Start Apache Kafka
   - Start Eureka Server (on port 8761)

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Start Microservices** (in order)
   ```bash
   # Terminal 1 - User Service
   mvn spring-boot:run -pl user-service
   
   # Terminal 2 - Restaurant Service
   mvn spring-boot:run -pl restaurant-service
   
   # Terminal 3 - Order Service
   mvn spring-boot:run -pl order-service
   
   # Terminal 4 - Payment Service
   mvn spring-boot:run -pl payment-service
   
   # Terminal 5 - Delivery Service
   mvn spring-boot:run -pl delivery-service
   
   # Terminal 6 - Notification Service
   mvn spring-boot:run -pl notification-service
   
   # Terminal 7 - API Gateway
   mvn spring-boot:run -pl api-gateway
   ```

## Service Details

### User Service (Port: 8081)
Manages user authentication, registration, and profile management.

**Key Endpoints**:
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `GET /api/users/{userId}` - Get user profile
- `PUT /api/users/{userId}` - Update user profile
- `GET /api/users/admin/**` - Admin operations (requires ADMIN role)

**Database**: user_db

---

### Restaurant Service (Port: 8082)
Handles restaurant information and menu management.

**Key Endpoints**:
- `GET /api/restaurants` - List all restaurants
- `POST /api/restaurants` - Create restaurant
- `GET /api/restaurants/{restaurantId}` - Get restaurant details
- `GET /api/menu-items/{restaurantId}` - Get menu items
- `POST /api/menu-items` - Add menu item

**Database**: restaurant_db

---

### Order Service (Port: 8083)
Manages order creation, tracking, and status updates.

**Key Endpoints**:
- `POST /api/orders` - Create new order
- `GET /api/orders/{orderId}` - Get order details
- `GET /api/orders/customer/{customerId}` - Get customer orders
- `PUT /api/orders/{orderId}/status` - Update order status
- `PUT /api/orders/{orderId}/assign-delivery` - Assign delivery person

**Database**: order_db
**Messaging**: Publishes order events to Kafka

---

### Payment Service (Port: 8084)
Processes payments and manages transactions.

**Key Endpoints**:
- `POST /api/payments` - Process payment
- `GET /api/payments/{paymentId}` - Get payment details
- `POST /api/payments/{paymentId}/refund` - Refund payment
- `GET /api/payments/order/{orderId}` - Get order payments

**Database**: payment_db
**Messaging**: Consumes order events, publishes payment events

---

### Delivery Service (Port: 8086)
Tracks deliveries and manages delivery personnel.

**Key Endpoints**:
- `POST /api/deliveries` - Create delivery
- `GET /api/deliveries/{deliveryId}` - Get delivery details
- `PUT /api/deliveries/{deliveryId}/status` - Update delivery status
- `PUT /api/deliveries/{deliveryId}/assign-person` - Assign delivery person
- `PUT /api/deliveries/{deliveryId}/location` - Update location
- `GET /api/deliveries/delivery-person/{deliveryPersonId}` - Get deliveries by person

**Database**: delivery_db
**Messaging**: Consumes order events

---

### Notification Service (Port: 8085)
Sends email and SMS notifications for order and delivery updates.

**Key Endpoints**:
- `POST /api/notifications` - Send notification
- `GET /api/notifications/{notificationId}` - Get notification
- `GET /api/notifications/user/{userId}` - Get user notifications
- `GET /api/notifications/user/{userId}/unread` - Get unread notifications
- `POST /api/notifications/retry-failed` - Retry failed notifications

**Database**: notification_db
**Messaging**: Consumes events from all services

**Configuration** (application.yml):
```yaml
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

---

### API Gateway (Port: 8080)
Central entry point for all client requests with JWT authentication.

**Features**:
- Request routing to appropriate microservices
- JWT token validation
- Load balancing with Eureka
- Centralized authentication

**Routes**:
- `/api/users/**` → User Service
- `/api/auth/**` → User Service
- `/api/orders/**` → Order Service
- `/api/restaurants/**` → Restaurant Service
- `/api/menu-items/**` → Restaurant Service
- `/api/payments/**` → Payment Service
- `/api/deliveries/**` → Delivery Service

---

## Authentication

The application uses JWT (JSON Web Tokens) for authentication.

### Getting a Token
1. Register a new user:
   ```bash
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json
   
   {
     "email": "user@example.com",
     "password": "password123",
     "firstName": "John",
     "lastName": "Doe"
   }
   ```

2. Login to get token:
   ```bash
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json
   
   {
     "email": "user@example.com",
     "password": "password123"
   }
   ```

3. Use token in requests:
   ```bash
   Authorization: Bearer <your-jwt-token>
   ```

## API Documentation

Each service exposes Swagger/OpenAPI documentation:

- **API Gateway Swagger**: http://localhost:8080/swagger-ui.html
- **User Service Swagger**: http://localhost:8081/swagger-ui.html
- **Restaurant Service Swagger**: http://localhost:8082/swagger-ui.html
- **Order Service Swagger**: http://localhost:8083/swagger-ui.html
- **Payment Service Swagger**: http://localhost:8084/swagger-ui.html
- **Notification Service Swagger**: http://localhost:8085/swagger-ui.html
- **Delivery Service Swagger**: http://localhost:8086/swagger-ui.html

## Service Discovery

**Eureka Dashboard**: http://localhost:8761

All services automatically register with Eureka for service discovery and load balancing.

## Messaging

The application uses Apache Kafka for asynchronous communication between services.

**Topics**:
- `order-events` - Order creation and status updates
- `payment-events` - Payment processing events
- `delivery-events` - Delivery status updates

## Database Schema

Each service has its own database with auto-generated schema using Hibernate DDL:

- **user_db**: Users, Addresses, Roles
- **restaurant_db**: Restaurants, MenuItems, Categories
- **order_db**: Orders, OrderItems, OrderStatus
- **payment_db**: Payments, Transactions, PaymentMethods
- **delivery_db**: Deliveries, DeliveryPersons, Locations
- **notification_db**: Notifications, NotificationLogs

## Configuration

### JWT Configuration
Update in each service's `application.yml`:
```yaml
jwt:
  secret: your-secret-key-change-this-in-production
  expiration: 86400000 # 24 hours
```

### Database Configuration
Update in each service's `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/database_name
    username: root
    password: root
```

### Kafka Configuration
Update in services that use Kafka:
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
```

## Troubleshooting

### Services not registering with Eureka
- Ensure Eureka Server is running on port 8761
- Check network connectivity
- Verify `eureka.client.service-url.defaultZone` in application.yml

### Database connection errors
- Verify MySQL is running
- Check database credentials in application.yml
- Ensure databases are created

### Kafka connection errors
- Verify Kafka is running on port 9092
- Check Kafka broker configuration
- Ensure Zookeeper is running

### JWT authentication failures
- Verify token format: `Bearer <token>`
- Check token expiration
- Ensure JWT secret matches across services

## Development Notes

### Adding a New Service
1. Create new module directory
2. Create pom.xml with parent reference
3. Create main application class with `@SpringBootApplication`
4. Create `application.yml` with service configuration
5. Add module to parent pom.xml
6. Implement controllers, services, and repositories

### Exception Handling
Each service includes a `GlobalExceptionHandler` for consistent error responses.

### Logging
Configure logging levels in `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.fooddelivery: DEBUG
```

## Technology Stack

| Component | Version |
|-----------|---------|
| Spring Boot | 3.2.0 |
| Spring Cloud | 2023.0.0 |
| Java | 17 |
| MySQL | 8.0.33 |
| Apache Kafka | Latest |
| JWT | 0.11.5 |
| Swagger/OpenAPI | 2.2.0 |
| Maven | 3.6+ |

## Future Enhancements

- [ ] Review and Rating Service
- [ ] Real-time notifications using WebSockets
- [ ] Advanced analytics and reporting
- [ ] Machine learning for delivery optimization
- [ ] Mobile app integration
- [ ] Integration with actual payment gateways (Razorpay, Stripe)
- [ ] Redis caching for improved performance
- [ ] Distributed tracing with Spring Cloud Sleuth
- [ ] Circuit breaker pattern with Resilience4j

## License

This project is licensed under the MIT License.

## Support

For issues or questions, please refer to the MODULES_SUMMARY.md file for detailed module documentation.
