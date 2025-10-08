This is the grammar-corrected and attractively formatted version of your README.md, incorporating all your specific requirements and deployment details.

🚀 User Management APP
This is a robust RESTful User Management API built with Spring Boot, secured using JWT (JSON Web Tokens), and deployed on the Railway cloud platform.

✨ Features & Endpoint Access
The API enforces Role-Based Access Control (RBAC) via JWT tokens, strictly defining access for administrators and regular users.

Endpoint	Method	Access Authority	Purpose
/register	POST	Method
permitAll() (Public)	Registers a new USER or ADMIN account.

/login	POST	Method
permitAll() (Public)	Authenticates a user and generates the JWT Token.

/users	GET	'Method
ADMIN	Retrieves a list of all user accounts.

/users/{id}	GET Method 
for ADMIN	Allows an administrator to manage view/any user by ID.

/users/{id}	GET Method 
for USER	it allows an view their informstion by ID to manage view/any user by ID.

/delete/{id}	DELETE	Method 
for ADMIN	Allows an administrator to delete any user by ID.

💻 Tech Stack
Category	Technology
Backend	Spring Boot
Security	Spring Security, JWT, BCrypt
Database	MySQL
ORM	Spring Data JPA / Hibernate
Deployment	Railway
⚙️ Deployment & Environment Configuration
This application relies on specific environment variables and a custom connection string for successful deployment on Railway, addressing common connectivity failures.

1. Application Configuration (application.properties)
The configuration uses the publicly exposed MySQL URL from Railway to prevent UnknownHostException errors and includes necessary SSL parameters.

Properties
# --- Critical Database Configuration for Railway ---
spring.datasource.url=jdbc:mysql://${MYSQLHOST}:${MYSQLPORT}/${MYSQL_DATABASE}?useSSL=true&requireSSL=true&verifyServerCertificate=false&allowPublicKeyRetrieval=true
spring.datasource.username=${MYSQLUSER}
spring.datasource.password=${MYSQLPASSWORD}

# --- JPA/Hibernate Settings ---
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# --- JWT Settings ---
You must manually set the following variables on your Web Service in the Railway dashboard:
Variable Name	Type	Value / Reference	Notes
MYSQLDATABASE: railway
MYSQLHOST	: turntable.proxy.rlwy.net	CRITICAL: Manually set to the public hostname (from MYSQL_PUBLIC_URL).
MYSQLPORT	: 26229	CRITICAL: Manually set to the public port (from MYSQL_PUBLIC_URL).
MYSQL_DATABASE:	Reference	${{Mysql.MYSQL_DATABASE}}	Standard reference to your MySQL service.
MYSQLUSER	Reference	${{Mysql.MYSQLUSER}}	Standard reference to your MySQL service.
MYSQLPASSWORD	Reference	${{Mysql.MYSQLPASSWORD}}	Standard reference to your MySQL service.
JWT_SECRET	Static	YourSecretKey12345...	Used for signing JWTs.
🔑 Spring Security Endpoints
The security configuration (SecurityConfig.java) is set to allow specific public access and enforce RBAC via JWT token validation.

Domain:
https://user-manaegement-app-production.up.railway.app

Endpoint	Method	Required Authority	Purpose
/register	POST	permitAll() (Public)	Create a new user account.
/login	POST	permitAll() (Public)	Authenticate user and receive JWT.
/users	GET	ADMIN	View list of all users.
/users/{id}	GET/PUT/DELETE	USER, ADMIN	Manage specific user profile.
Any other path		authenticated()	Requires a valid JWT token.
▶️ Testing the API (Postman)
Use Postman or a similar tool to interact with the deployed API endpoints.

1. Register a User (Public)
Method: POST

URL: [DOMAIN]/register

{
    "email": "testuser12@example.com",
    "password": "password123",
    "roles": ["USER"]
}

2. Login and Get Token (Public)
Method: POST

URL: [DOMAIN]/login

{
    "email": "testuser12@example.com",
    "password": "password123"
}

Response: You will receive a JWT token in the response body.


3. Access Protected Endpoint (Requires Token)
All protected requests must include the JWT token in the format:

Header: Authorization: Bearer <JWT TOKEN>

Endpoint Example	Method
[DOMAIN]/users	GET
[DOMAIN]/users/1	GET
[DOMAIN]/delete/1	DELETE
