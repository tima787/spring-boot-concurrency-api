# Project Title

Customer Portfolio Aggregation Service

---

## 🚀 Features

* **Feature:** Portfolio Aggregation Service: one API that fans out to the four downstream services concurrently, assembles a consolidated view, and returns it to the client

## 🛠️ Tech Stack

* **Java Version:** 21
* **Framework:** Spring Boot 3.5.x
* **Build Tool:** Maven
* **Database:** Mock Data

## 📋 Prerequisites

Before running this project, ensure you have the following installed:
* [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/) (Version 21 or higher)
* [Spring Tool Suite (STS)](https://spring.io/tools) or Eclipse
* [Maven](https://maven.apache.org/)

## ⚙️ Getting Started

### 1. Unzip portfolio-aggregator

### 2. Import into STS
1. Open **Spring Tool Suite**.
2. Go to **File** > **Import...**
3. Select **Existing Maven Projects** and click **Next**.
4. Browse to the root directory of the project and click **Finish**.

### 3. Run the Application
* **Via STS:** Right-click the project root > **Run As** > **Spring Boot App**.
* **Via CLI (Maven):**
  ```bash
  ./mvnw spring-boot:run
  ```

---

## 🔌 API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/v1/customers/{customerId}/portfolio` | Get pors |

---

## 🗂️ Project Structure

```portfolio-aggregator
├── src/main/java
│   └── com/wealth/portfolio/
│       ├── config/      	 # Bean Configuration Setup
│       ├── controller/      # REST Controllers (API Endpoints)
│       ├── service/         # Business Logic Layer
│       └── domain/          # POJOs
└── src/main/resources
    ├── application.properties  # App configurations
    └── templates/              # UI templates (if applicable)
```
