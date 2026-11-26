# Product Management & E-Commerce System

A full-stack product management and e-commerce application built with **Spring Boot, React, and MySQL**. The system provides an admin dashboard for managing products and inventory, along with a customer catalogue and Cash on Delivery (COD) checkout.

## Features

### Admin dashboard
- Dashboard statistics
- Product management: create, view, update, and delete products
- Product search, pagination, and sorting
- Inventory management
- Add or reduce stock
- Low-stock, out-of-stock, and in-stock product views
- Order management
- Update order status
- Admin authentication and protected management APIs

### Customer features
- Browse the product catalogue
- View product information
- Add products to the cart
- Update or remove cart items
- Enter customer and delivery details
- Place orders using Cash on Delivery (COD)

### Order and inventory management
- Store customer orders in MySQL
- View orders in the admin dashboard
- Update order status
- Integrate order processing with inventory management

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | React 19, Vite, React Router |
| API communication | Axios |
| Backend | Java 21, Spring Boot 4.1.1 |
| REST API | Spring Web MVC |
| Database | MySQL 8.0 |
| Persistence | Spring Data JPA, Hibernate |
| Security | Spring Security, HTTP Basic Authentication |
| Validation | Jakarta Bean Validation |
| Containerization | Docker, Docker Compose |
| Deployment | Railway |

## Project Structure

```text
Web/
├── src/
│   └── main/
│       └── java/com/su/Web/
│           ├── Controller/
│           ├── DTO/
│           ├── Model/
│           ├── Repository/
│           ├── Service/
│           └── Config/
├── product-frontend/
│   └── src/
│       ├── components/
│       ├── services/
│       ├── App.jsx
│       └── App.css
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

## Prerequisites

Install the following tools:

- Java 21
- Node.js and npm
- MySQL 8.0, or Docker Desktop
- Git

## Run Locally

### 1. Clone the repository

```bash
git clone https://github.com/gannu9/product-management-ecommerce-system.git
cd product-management-ecommerce-system
```

### 2. Configure the database

The project includes a Docker Compose configuration for MySQL and the backend.

Create a `.env` file in the project root:

```env
DB_PASSWORD=your_local_database_password
```

Do not commit this file. Keep database credentials and other secrets out of GitHub.

### 3. Start MySQL and the backend with Docker

```bash
docker compose up --build
```

The services use these local ports:

- MySQL: `localhost:3307`
- Backend API: `http://localhost:8080`

To stop the services:

```bash
docker compose down
```

To stop the services while retaining the database volume, use the command above without `-v`.

### 4. Run the backend without Docker

Configure the database connection in:

`src/main/resources/application.properties`

Set the appropriate database URL, username, and password for your local MySQL instance. Configure the admin credentials using the environment variables expected by the application's security configuration.

Run the backend:

```bash
./mvnw spring-boot:run
```

The backend runs on:

`http://localhost:8080`

### 5. Run the React frontend

Open a second terminal:

```bash
cd product-frontend
npm install
npm run dev
```

Vite will display the local frontend URL in the terminal. The development server may use port `5173` or another available port.

The frontend API service is located at:

`product-frontend/src/services/productService.js`

Update its backend base URL when connecting the frontend to a different backend environment.

## API Documentation

Base backend URL:

```text
http://localhost:8080
```

### Product APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/products` | Get all products |
| GET | `/products/{prodId}` | Get product by ID |
| GET | `/products/page` | Get paginated products |
| GET | `/products/search?keyword={keyword}` | Search products |
| GET | `/products/sort` | Get paginated and sorted products |
| GET | `/customer/products` | Get customer catalogue |
| POST | `/products` | Create a product |
| PUT | `/products` | Update a product |
| DELETE | `/products/{prodId}` | Delete a product |

### Dashboard and inventory APIs

| Method | Endpoint | Description |
|---|---|---|
| GET | `/products/dashboard` | Get dashboard statistics |
| GET | `/products/low-stock` | Get low-stock products |
| GET | `/products/out-of-stock` | Get out-of-stock products |
| GET | `/products/in-stock` | Get in-stock products |
| PATCH | `/products/{prodId}/add-stock?quantity={quantity}` | Add inventory |
| PATCH | `/products/{prodId}/reduce-stock?quantity={quantity}` | Reduce inventory |

### Order APIs

Base path:

```text
/api/orders
```

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/orders/checkout` | Place a customer COD order |
| GET | `/api/orders` | Get all orders |
| GET | `/api/orders/{orderId}` | Get an order by ID |
| PATCH | `/api/orders/{orderId}/status` | Update order status |

Admin order and product management APIs require authentication. Customer catalogue and checkout endpoints are intended for public access.

## Deployment

The application is deployed using Railway.

### Backend and database

- Backend service: Railway
- Database: MySQL
- Backend API URL:

```text
https://product-management-ecommerce-system-production.up.railway.app
```

Configure the required database and admin authentication environment variables in the Railway service settings. Do not place secrets in source code or commit them to GitHub.

### Frontend

The React frontend is deployed separately and configured to communicate with the Railway backend.

Update the API base URL in:

`product-frontend/src/services/productService.js`

when deploying to a different backend environment.

Ensure the deployed frontend origin is permitted by the backend CORS configuration.

## Security Notes

- Admin APIs are protected using Spring Security and HTTP Basic Authentication.
- Admin credentials are configured through environment variables.
- Never commit `.env` files, database passwords, or admin credentials.
- Use HTTPS for deployed environments.
- Do not share customer or order personal information in public screenshots or logs.

## Testing and Build

### Backend tests

```bash
./mvnw test
```

### Frontend production build

```bash
cd product-frontend
npm run build
```

### Frontend lint

```bash
npm run lint
```

## Future Improvements

Potential improvements may include additional automated tests, enhanced customer authentication, and further UI refinements.

## Author

**Gagan Sharma**

GitHub: [gannu9](https://github.com/gannu9)
