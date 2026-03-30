# Food Delivery Application - Modules Summary

## Overview
This is a microservices-based food delivery application built with Spring Boot and Spring Cloud. The application consists of multiple independent services that communicate with each other through REST APIs and Kafka messaging.

## Modules

### 1. User Service (Port: 8081)
**Purpose**: Handles user authentication, registration, and profile management

**Components**:
- `UserServiceApplication.java` - Main application class
- `UserController.java` - REST endpoints for user operations
- `AuthController.java` - Authentication endpoints
- `UserService.java` - Business logic for user management
- `User.java` - User entity
- `Address.java` - Address entity
- `UserRole.java` - User role enum
- `SecurityConfig.java` - Spring Security configuration
- DTOs for request/response handling
- Exception handling classes
- Repositories for database operations

**Database**: user_db
**Key Features**:
- User registration and login
- JWT-based authentication
- User profile management
- Role-based access control

---

### 2. Restaurant Service (Port: 8082)
**Purpose**: Manages restaurant information, menu items, and availability

**Components**:
- `RestaurantServiceApplication.java` - Main application class
- `RestaurantController.java` - REST endpoints for restaurant operations
- `MenuItemController.java` - REST endpoints for menu items
- `RestaurantService.java` - Business logic
- `MenuItemService.java` - Menu item management logic
- Entity classes for Restaurant and MenuItem
- Repositories for database operations
- Exception handling
- `RestTemplateConfig.java` - REST client configuration

**Database**: restaurant_db
**Key Features**:
- Restaurant management
- Menu item management
- Restaurant availability tracking
- Search and filter capabilities

---

### 3. Order Service (Port: 8083)
**Purpose**: Handles order creation, management, and status tracking

**Components**:
- `OrderServiceApplication.java` - Main application class
- `OrderController.java` - REST endpoints for order operations
- `OrderService.java` - Business logic for order management
- `Order.java` - Order entity
- `OrderItem.java` - Order item entity
- `OrderStatus.java` - Order status enum
- `PaymentStatus.java` - Payment status enum
- `OrderEvent.java` - Event class for Kafka messaging
- DTOs for request/response handling
- Repositories for database operations
- Exception handling
- `RestTemplateConfig.java` - REST client configuration

**Database**: order_db
**Kafka Integration**: Publishes order events to Kafka
**Key Features**:
- Order creation with items
- Order status management
- Delivery person assignment
- Order history and analytics
- Revenue calculation

---

### 4. Payment Service (Port: 8084)
**Purpose**: Handles payment processing and transaction management

**Components**:
- `PaymentServiceApplication.java` - Main application class
- `PaymentController.java` - REST endpoints for payment operations
- `PaymentService.java` - Business logic for payment processing
- Payment entity classes
- Transaction entity classes
- Repositories for database operations
- Exception handling
- `RestTemplateConfig.java` - REST client configuration

**Database**: payment_db
**Kafka Integration**: Consumes payment events and publishes payment status updates
**Key Features**:
- Payment processing
- Multiple payment gateway support (Razorpay, Stripe)
- Transaction tracking
- Refund management

---

### 5. Delivery Service (Port: 8086)
**Purpose**: Manages delivery tracking, delivery person assignment, and delivery status

**Components**:
- `DeliveryServiceApplication.java` - Main application class
- `DeliveryController.java` - REST endpoints for delivery operations
- `DeliveryService.java` - Business logic for delivery management
- `Delivery.java` - Delivery entity
- `DeliveryPerson.java` - Delivery person entity
- `DeliveryStatus.java` - Delivery status enum
- `DeliveryPersonStatus.java` - Delivery person status enum
- `PickupLocation.java` - Pickup location entity
- `DeliveryLocation.java` - Delivery location entity
- DTOs: `DeliveryRequestDto`, `DeliveryResponseDto`, `DeliveryStatusUpdateDto`
- Repositories for database operations
- Exception handling: `DeliveryNotFoundException`, `InvalidDeliveryStateException`, `GlobalExceptionHandler`
- `RestTemplateConfig.java` - REST client configuration

**Database**: delivery_db
**Kafka Integration**: Consumes delivery events
**Key Features**:
- Delivery creation and assignment
- Real-time location tracking
- Delivery status updates
- OTP verification
- Delivery person performance metrics
- Delivery history and analytics

---

### 6. Notification Service (Port: 8085)
**Purpose**: Sends notifications via email and SMS for order and delivery updates

**Components**:
- `NotificationServiceApplication.java` - Main application class
- `NotificationController.java` - REST endpoints for notification operations
- `NotificationService.java` - Business logic for notification management
- `Notification.java` - Notification entity
- `NotificationType.java` - Notification type enum
- `NotificationStatus.java` - Notification status enum
- DTOs: `NotificationDto`, `SendNotificationDto`
- `NotificationRepository.java` - Database operations
- Email sending functionality

**Database**: notification_db
**Kafka Integration**: Consumes events from order, payment, and delivery services
**Key Features**:
- Email notifications
- SMS notifications
- Notification status tracking
- Retry mechanism for failed notifications
- Notification history

---

### 7. API Gateway (Port: 8080)
**Purpose**: Central entry point for all client requests, handles routing and JWT authentication

**Components**:
- `ApiGatewayApplication.java` - Main application class
- `JwtAuthenticationFilter.java` - JWT validation filter

**Key Features**:
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

## Service Ports Summary
| Service | Port |
|---------|------|
| API Gateway | 8080 |
| User Service | 8081 |
| Restaurant Service | 8082 |
| Order Service | 8083 |
| Payment Service | 8084 |
| Notification Service | 8085 |
| Delivery Service | 8086 |

---

## Infrastructure Requirements

### Databases
- MySQL 8.0+ with the following databases:
  - user_db
  - restaurant_db
  - order_db
  - payment_db
  - delivery_db
  - notification_db

### Message Queue
- Apache Kafka (localhost:9092)
- Topics: order-events, payment-events, delivery-events

### Service Discovery
- Eureka Server (localhost:8761)

### Email Configuration
- SMTP server for sending notifications
- Configure in notification-service application.yml

---

## Technology Stack
- **Framework**: Spring Boot 3.2.0
- **Cloud**: Spring Cloud 2023.0.0
- **Database**: MySQL 8.0.33
- **Message Queue**: Apache Kafka
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT (jjwt 0.11.5)
- **API Documentation**: Springdoc OpenAPI (Swagger)
- **Build Tool**: Maven
- **Java Version**: 17

---

## Configuration Files
Each service has an `application.yml` file with:
- Server port configuration
- Database connection details
- Eureka client configuration
- Kafka configuration (where applicable)
- JWT secret configuration
- Swagger/OpenAPI configuration

---

## Running the Application

### Prerequisites
1. Install Java 17
2. Install MySQL 8.0+
3. Install Apache Kafka
4. Install Eureka Server

### Steps
1. Create databases for each service
2. Start Eureka Server
3. Start Kafka
4. Build the parent project: `mvn clean install`
5. Start each service in the following order:
   - User Service
   - Restaurant Service
   - Order Service
   - Payment Service
   - Delivery Service
   - Notification Service
   - API Gateway

### Access Points
- API Gateway: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html (for each service individually)
- Eureka Dashboard: http://localhost:8761

---

## API Documentation
Each service exposes Swagger/OpenAPI documentation at:
- `http://localhost:{port}/swagger-ui.html`
- `http://localhost:{port}/api-docs`

---

## Future Enhancements
- Review Service (commented out in pom.xml)
- Real-time notifications using WebSockets
- Advanced analytics and reporting
- Machine learning for delivery optimization
- Mobile app integration
- Payment gateway integration with actual providers
