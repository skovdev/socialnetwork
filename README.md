# SocialNetwork

Pet-project for self-education, designed to simulate a basic social media application built with modern Java and Spring Cloud technologies.

## Overview

The **SocialNetwork** project is a microservices-based system built with Java 21, Spring Boot, and Docker.  
It demonstrates user authentication, profile management, real-time notifications, service discovery, centralized configuration, and event-driven communication.

## Architecture

- **Backend Microservices**:
  - `socialnetwork-auth-server`: Manages authentication and token issuing (JWT)
  - `socialnetwork-user-service`: Manages user data
  - `socialnetwork-profile-service`: Handles user profiles and updates
- **Infrastructure**:
  - `socialnetwork-api-gateway`: API Gateway for routing client requests
  - `socialnetwork-service-discovery`: Eureka server for service registry
  - `socialnetwork-config-server`: Centralized configuration server
  - `kafka`: Messaging system for real-time communication
  - `postgresql`: Database for data persistence
- **Frontend**:
  - `socialnetwork-app-client`: Client-side application (React.js)

## Technologies

- Java 21
- Spring Boot
- Spring Cloud
- Spring Security (JWT)
- PostgreSQL
- Apache Kafka
- Docker, Docker Compose
- React.js

## Author

Created by [Stanislav Kovalenko](https://github.com/skovdev) for educational purposes and personal growth.
