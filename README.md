# IDP – Event Manager Microservices Project

Project for University.

## 🚀 Quickstart

### 1. Build All Backend Services

Some tests(event manager's to my knowledge) are currently unstable and may fail, so use:

```
mvn clean package -DskipTests
```

Run this command **in each backend service folder** (`auth-service`, `event-service`, etc).

---

### 2. Start the Backend (Gateway + Microservices)

From the `gateway` directory, start everything with Docker Compose:

```
docker compose up --build
```

### 3. Start the Frontend

In the `event-manager-frontend` folder:

```
npm install
npm start
```

### 4. (Mailhog Notifications

- **Mailhog (Email testing):** Currently sending all mails to "me" actual email addresses to be obtained and used soon™.

---

## 🔗 Ports

| Service           | Port     | URL                                 |
|-------------------|----------|--------------------------------------|
| API Gateway       | 8081     | http://localhost:8081                |
| Frontend (React)  | 3000     | http://localhost:3000                |
| Mailhog           | 8025     | http://localhost:8025                |

---

## 📝 Notes

- All backend requests should go through the **Gateway** (`localhost:8081`).
- If you encounter issues with tests, skip them as above; this will be fixed in the future.

---

Enjoy! If you have questions or want to contribute, feel free to open an issue or PR.
