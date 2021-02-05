# PSA-Backend

```bash
#Start Server
$mvn boot-spring:run
```

How to connect to local mysql database
1. Open MYSQL workbench
2. Enter following to create a database and use it
```bash
CREATE DATABASE ${any name you like};
USE ${any name you like};
```
3. Enter the following in PSABackend/resources/application.properties
```bash
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/${name you put in step 2}
spring.datasource.username=root
spring.datasource.password=${your password}
```

4. Go back to the workbench and insert SQL
```bash
CREATE TABLE users(
id int(11) NOT NULL,
active int(1),
password varchar(11),
roles varchar(11),
user_name varchar(11),
primary key (id)
);
```

5. Run following command to insert first user
```bash
curl -d '{"active": 0, "password": "Password1", "roles": "User", "user_name": "SHAUN"}' -H 'Content-Type: application/json' http://localhost:8080/add
```

6. Run following command to get all users in user table
```bash
curl http://localhost:8080/all
```