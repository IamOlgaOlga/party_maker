
## Overview

*Please read the whole document carefully*

Using Spring Boot we would like you to design and build a microservice to help organise corporate parties. Your service will need to expose a RESTful API to manage the guest list.  
IMPORTANT: The number of tables and seat capacity are subject to change.

When the party begins, guests will arrive with their friends. This number may not be the size indicated on the guest list. However, if it is expected that the guest's table can accommodate the extra people, then they all should be let in. Otherwise, they will be turned away.
Guests will also leave throughout the course of the party. Note that when a guest leaves, their accompanying guests will leave with them.

At any point in the party, we should be able to know:
- Our guests at the party
- How many empty seats there are

Hopefully this tech task allows you to show yourself off as much as you decide to!  
How long you spend on this is up to you, but we suggest spending no more than two or three hours.

## Sample API guide

This is a directional API guide.

### Add a guest to the guestlist

If there is insufficient space at the specified table, then an error should be thrown.

```
POST /guest_list/name
body: 
{
    "table": int,
    "accompanying_guests": int
}
response: 
{
    "name": "string"
}
```

### Get the guest list

```
GET /guest_list
response: 
{
    "guests": [
        {
            "name": "string",
            "table": int,
            "accompanying_guests": int
        }, ...
    ]
}
```

### Guest Arrives

A guest may arrive with his friends that are not the size indicated at the guest list.
If the table is expected to have space for the extras, allow them to come. Otherwise, this method should throw an error.

```
PUT /guests/name
body:
{
    "accompanying_guests": int
}
response:
{
    "name": "string"
}
```

### Guest Leaves

When a guest leaves, all their accompanying friends leave as well.

```
DELETE /guests/name
```

### Get arrived guests

```
GET /guests
response: 
{
    "guests": [
        {
            "name": "string",
            "accompanying_guests": int,
            "time_arrived": "string"
        }
    ]
}
```

### Count number of empty seats

```
GET /seats_empty
response:
{
    "seats_empty": int
}
```

## Deliverables
Please return a zip file containing the *repo.bundle* resulting from the following command:  
`git bundle create repo.bundle --branches --tags`
  
Make sure to include:
- All source code you produced
- A comprehensive README file
- If using containers, a Compose file to run your project

# Assessment
Your project is going to be assessed as per the following criteria:
1. Your project must build and run on Unix-like machines (Linux or Mac OS)
1. The service meets the described requirements
1. Good documentation to get us started and understand your work
1. Good API documentation
1. Use of atomic and descriptive git commits
1. Quality and coverage of your tests

# General notes
- We bootstrapped the project for you with Spring Boot 3.2 and a maven wrapper; use Java 21.
- You can use any libraries you would normally use.
- You should write as many tests as you feel are necessary, but your code must compile, and the
tests must pass.
- You can assume your API will be public for this assignment; there is no need to handle security.
- If any part of the assignment is not clear you can make assumptions; we would appreciate if you
made note of them in the README.md file.
- Feel free to note any future improvements you could make to your service that were out of the
scope of this assignment.
- Extra points for using PostgreSQL.
- Extra points for using containers.

# Development notes

## How to build the application

Application is build with Maven, use `mvn clean install -Dspring.profiles.active=test` command to build it.

`test` profile is used in order to skip running Flyway database schema migrations which are not needed
for unit tests which are run during application build.

## How to run the application

The application runs in a Docker container.

There is a `Dockerfile` which is used to build an application Docker image and there is a `docker-compose.yml` file
to run the application and PostgreSQL containers together.

Use `docker-compose up --build` command to start the environment (database and the application itself).

Be sure to build the Spring Boot app (`mvn clean install -Dspring.profiles.active=test`) before the first run of docker-compose,
or if you made any changes to the application or its configuration.

In order to run docker-compose successfully you need to have localhost ports 8080 and 5432 available
for the application and the database accordingly.

To stop the application you can either use `^C` from the same terminal tab you started the app,
or use `docker-compose down` command.

## Application configuration

By default, the application will run as is without any configuration changes via `docker-compose`.

You can find database configuration values in `application-docker.properties`
(when run with Docker, `docker` Spring profile is used) file and in `docker-compose.yml`.

If you want to run the application without Docker, change the DB configuration values in `application.properties` file 
as per your locally running PostgreSQL database which you need to initialize and configure manually.

## Database schema migrations

Flyway is used for DB migrations. All migration scripts are in `/db/migration` directory.

In case of running the application not as a Docker container, you still need to create `exercisedb` database
with username `exercise` and password `password` (or change these values in `application.properties`).

## Application API usage and documentation

There is a Postman collection (for `Guest Controller` and `Table Controller`) and environment files located in `/postman` directory.

To see API documentation, visit: http://localhost:8080/swagger-ui/index.html

## Misc notes

### Changing tables capacity

Initially, there are three values added to the database `tables` table (via Flyway migration script):

- `(table id = 1, capacity = 5)`
- `(table id = 2, capacity = 10)`
- `(table id = 3, capacity = 15)`

I also added a `TableController` with a few example methods to work with tables (see Swagger documentation).

### Other assumptions/thoughts

- From the task's context I decided that a guest's name is a primary key. 
There is no chance that we can book 2 guests with the same name. 
I decided that in a moment only one guest with some name could try to book a table (like in a real life), 
so there is no data race possible for this case.
- I created interfaces only for controller classes. Because this is a small application 
and I don't need to extend functionality or add new implementations in the future. 
Interfaces for controllers are useful because of using Swagger for API documentation.
It's easier to read code and fix something if we separate Spring and Swagger annotations.
- For DTO classes in order to write less code I decided not to create a separate class for each kind of request/response.
I use the same class for multiple requests/responses.
But if it's more clear for developers (or in case of a bigger project) I can create separate classes for each request/response.
This decision is imperfect and was made to save some time. It can be further improved by providing each request with 
its own class and specific validation rules on each field which will result in more detailed error messages.