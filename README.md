Spring-boot-Jwt-Docker-Starter-Project

## 📦 Requirements

Before running this project, make sure you have the following installed:

### 1. Docker Desktop
This project runs entirely inside Docker containers.
Make sure Docker is running before continuing.

## ▶️ Run the Project

After installing Docker Desktop and starting Docker, run the following command in the project root:

```bash
docker compose up --build
```

## 🌐 Ports & Services

Once the containers are running, the following services are available on your local machine:

| Service        | URL / Port                  |
|---------------|-----------------------------|
| Spring Boot API | http://localhost:9090       |
| phpMyAdmin     | http://localhost:8081       |
| MySQL          | localhost:3307              |
| Redis          | localhost:6379              |

These ports are mapped from the Docker containers to your local host via `docker-compose.yml`.