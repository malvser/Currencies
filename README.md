# README.md - Currency API project description

## Project description

The Currency API is a REST API for managing a list of currencies and getting exchange rates for them. The project includes the following functions:

- Getting a list of currencies.
- Adding new currencies.
- Getting exchange rates for the specified currency.

---

## Using the Swagger UI

To view and test the API using the Swagger UI, follow the link:  
[Swagger UI](http://localhost:8087/swagger-ui/index.html)

Swagger is available after the program starts using port `8087` (may vary depending on the configuration).

---

## Configuring the database

The project uses **PostgreSQL** as a database. For ease of integration, the database is run via Docker using `docker-compose`.

### Contents of `docker-compose.yml`:

```
version: '3.8'

services:
  postgres:
    image: postgres:13
    container_name: currency-db
    environment:
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
      POSTGRES_DB: currencies_db
    ports:
      - "5432:5432"
    networks:
      - currency-network
    volumes:
      - postgres_data:/var/lib/postgresql/data

networks:
  currency-network:
    driver: bridge

volumes:
  postgres_data:
```

## How to assemble the project and launch it


### Prerequisites:
1. Java Development Kit (JDK) version 17 or later.
2. Gradle (if you do not use the built-in Gradle Wrapper).
3. Docker and Docker Compose.

### Steps:
1. Clone the repository:
```
git clone <repository-url>
cd <repository-directory>
```
2. Run the database using Docker Compose:

```
docker-compose up -d
```
3. Check if the database is working:
```
docker ps
```
The database should run on localhost:5432.

4. Assemble the project:
```
./gradlew build
```
5. Launch the project:

```
./gradlew bootRun
```

6. Open Swagger to view documentation: http://localhost:8087/swagger-ui/index.html



