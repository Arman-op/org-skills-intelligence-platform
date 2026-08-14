MySQL Setup for SILMS Backend

Follow these steps to prepare a local MySQL database and user for the backend.

1. Install MySQL Server (if not already installed). Ensure the server is running.

2. Connect to MySQL as a user with privileges (e.g., `root`) and run:

```sql
CREATE DATABASE silms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'silms'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON silms.* TO 'silms'@'localhost';
FLUSH PRIVILEGES;
```

3. Update the connection settings in `backend/src/main/resources/application.properties` if you changed the username/password or database name.

4. Build and run the backend with Maven:

```powershell
cd backend
mvn -DskipTests package
mvn spring-boot:run
```

Flyway will run the migration scripts found in `src/main/resources/db/migration` and create the schema.

If you prefer a different host or credentials, update `spring.datasource.url`, `spring.datasource.username`, and `spring.datasource.password` in `application.properties` accordingly.
