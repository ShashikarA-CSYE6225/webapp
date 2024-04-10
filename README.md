# Web App

## Overview

This web application provides a RESTful API for managing user accounts. Users can create an account, update their information, and retrieve their account details using the provided API endpoints. The application also supports token-based authentication to ensure secure access to user data.

## Requirements

- Java (jdk 17)
- Spring Boot (version 3.2.2)
- Database (MySQL)
- BCrypt for password hashing

## Getting Started

To get started with the web application, follow these steps:

1. **Clone the repository**: `git clone <repository_url>`
2. **Build the project**: `./mvnw install` (for Maven)
3. **Run the application**: `./mvnw spring-boot:run`

## API Endpoints

### Create a New User

- **Endpoint**: `/v1/user`
- **Method**: POST
- **Request Payload**:
  ```json
  {
    "email": "user@example.com",
    "password": "your_password",
    "first_name": "John",
    "last_name": "Doe"
  }
  ```
- **Response**: Returns the created user details without password with HTTP status code 200 OK.

### Update User Information

- **Endpoint**: `/v1/user/self`
- **Method**: PUT
- **Request Payload**: Fields to be updated (firstName, lastName, password)
- **Response**: Returns the updated user details with HTTP status code 204 NO CONTENT.

### Get User Information

- **Endpoint**: `/v1/user/self`
- **Method**: GET
- **Response**: Returns the user details (excluding password) with HTTP status code 200 OK.

## Authentication

- The application supports token-based authentication.
- Users must provide a basic authentication token when making API calls to authenticated endpoints. Only works for GET and PUT.

## Continuous Integration (CI) with GitHub Actions

- A GitHub Actions workflow is set up to run a simple check (compile code) for each pull request.
- Pull requests can only be merged if the workflow executes successfully.
- Status Checks GitHub branch protection is enabled to prevent users from merging a pull request when the GitHub Actions workflow run fails.
