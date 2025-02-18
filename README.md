# ACME College REST API

The ACME College REST API is designed to provide a scalable, efficient, and easy-to-use interface for managing student records, course information, and enrollment data for educational institutions. This project is built using modern RESTful practices and aims to simplify the integration of external applications with the ACME College student information system.

## Key Features
- **CRUD Operations**: Perform create, read, update, and delete operations on student records, courses, and enrollments.
- **RESTful Architecture**: Built with REST principles, allowing easy integration with other systems and services.
- **Authentication**: Secure endpoints with token-based authentication to ensure data integrity and security.
- **Scalability**: Designed to handle large datasets, making it suitable for institutions with high volumes of student and course data.
- **Extensibility**: Easily adaptable to accommodate additional data types or integration needs.

This API streamlines the management of college operations, reducing the complexity involved in handling data across different educational systems.

## Roles and Privileges
The system has two roles:
- **Admin**: Has full access to all CRUD operations on all entities.
- **User**: Has restricted access based on specific privileges for each entity.

## Technologies Used
- **Java**: Programming language
- **JPA**: Java Persistence API for managing relational data in Java applications
- **Hibernate**: Object-relational mapping for database interaction
- **JUnit**: Testing framework for unit tests

## Postman Collection
Use the provided [Postman Collection] to test the project's APIs. Import the collection into Postman and execute the requests to validate the functionality.
