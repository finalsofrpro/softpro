# Appointment Booking System - Unit Testing & Code Coverage

This project is a Java-based Appointment Booking System. The primary focus of this project is to demonstrate professional software testing techniques using **JUnit 5**, **Mockito**, and **JaCoCo**.

## Key Achievement
The project successfully achieved a **96% Code Coverage** for the core business logic (BookingService), exceeding the industry standard of 80%.

##  Technology Stack
* **Java 17**
* **Maven** (Project Management)
* **JUnit 5** (Testing Framework)
* **Mockito** (Mocking Framework)
* **JaCoCo** (Code Coverage Tool)

##  Implemented Test Cases
The `BookingServiceTest` class covers various scenarios to ensure system reliability:
1. **testBookSuccess**: Verifies that an appointment is successfully booked when all conditions are met.
2. **testBookFailureAlreadyBooked**: Ensures that the system prevents re-booking an already "BOOKED" appointment.
3. **testBookFailureStrategyInvalid**: Tests that the booking fails if the specific business strategy (BookingStrategy) returns invalid.
4. **testCancelSuccess**: Validates that an appointment is correctly released and updated to "AVAILABLE" status.
5. **testCancelFailureWrongUser**: Verifies that a user cannot cancel a booking they do not own.

##  Code Coverage Analysis
To ensure high-quality metrics, the report focuses on the **Services Layer** where the business logic resides.
* **Core Logic Coverage:** 96%
* **Branch Coverage:** 100% (All decision paths were tested)
* **Exclusions:** GUI, Models, and Repository layers were excluded as they contain boilerplate code or UI elements that do not require Unit Testing.

##  How to Run the Tests
To generate the coverage report, follow these steps:

1. **Run the tests using Maven:**
   ```bash
   mvn clean test


**Authors**

**Raghad Mansour 12324959**

**Farah Amer 12325248**