# AvA affordable versatile accommodations - accommodation booking service

<img src="logo.png" alt="logo" width="400" height="400">


## Description

"AvA" aims to revolutionize the housing rental experience by developing an advanced online management system for housing rentals.
This system simplifies the tasks of service administrators and provides renters with an efficient platform for securing accommodations.

## Main Features

### Accommodation management
Basic accommodation management, which includes the ability to create various accommodations (houses, apartments, condo, vacation home). It is also possible to update the address, availability and price separately.
Advanced accommodation management, which is designed to manage only apartments in Ukraine with the ability to process incoming data in a certain format set by the customer:
[
{
{...},
"city" : "City",
"street" : "Street st.",
"house" : "123a",
"size" : 2,
"amenities" : "string",
"price_per_month_usd" : 195.99
},
{...}
]

### Booking management
The possibility of creating/cancelling a reservation for further payment. And also with the ability to update status separately.

### Manage Users
Basic management with the possibility of updating authorizations

### Notification
Sending telegram bot messages to administrators when creating a new accommodations, bookings, payments.

### Registration and Authentication
A simple registration process that allows users to create an account.

### Handling payments
Creating and accepting payments using the payment system (Stripe).

### Security and Confidentiality
Assurance of the confidentiality of personal information and user security.

### Technologies
- Spring Boot
- Spring Security
- Spring Data JPA
- Spring MVC
- Swagger
- JWT
- Liquibase
- Lombok
- Mapstruct
- Postgres SQL
- Stripe API
- Telegram API
- Mockito
- Maven
- Docker

### Features
#### user:
- registration/authentication/getting/updating/updating role of user
#### accommodation:
- create/get/update/(soft)delete an accommodation
- create a list of accommodations
- get a list of all accommodations
- updating separately: price, availability, address
#### booking:
- create/cancel/get/update/(soft)delete a booking
- get a bookings by user id and booking status name or in booking status name only
- get a list of all bookings of the logged-in user
- update booking status
#### payment:
- initialize payment
- get a list of all payments of the logged-in user
- get a list of all payments of user by id
- handling successful/unsuccessful payment

### Startup instructions
...
### Postman
...
### Technical details
...