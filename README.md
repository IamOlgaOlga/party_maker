
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

- Number of tables and theirs capacity could be changes in `application.properties` file:
```
exercise.tables=10
exercise.capacity=4
```
- PostgreSQL is used for this application, please change DB url,username and password in `application.properties` file:
```
## PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/exercisedb
spring.datasource.username=exercise
spring.datasource.password=password
```
- Flyway is used for DB migrations. All migration scripts are in `/db/migration` path, also `FlywayConfig` was added.
