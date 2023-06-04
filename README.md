![Book Store](https://github.com/kadianKunal/OrderManagement/assets/35004605/bd7efd52-658b-4c2f-baee-ac7d24261e28)


# OrderManagement

The Order management Service is a microservice which is part of Online Bookstore.
It has RESTful API for managing orders. It provides endpoints to perform important operations on orders.

## Technologies Used

- Java
- Spring Boot
- Spring Data JPA
- H2 Database
- Maven

## Getting Started

To get started with the Order Service, follow the instructions below.

### Prerequisites

- Java Development Kit (JDK) 8 or higher installed
- Apache Maven installed

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Build the project using Maven: mvn clean install

### Usage
1. Start the application: mvn spring-boot:run
2. The Book Service will be accessible at http://localhost:9000 (this is port of api gateway, which is part of same microservice)
3. Use a REST client (e.g., Postman) to interact with the available endpoints

### Endpoints
The following API endpoints are available:

- `GET /orders`: Retrieves all orders.
- `GET /orders/{id}`: Retrieves an order by its ID.
- `POST /orders`: Creates a new order.
- `DELETE /orders/{id}`: Cancels an order.



      


