# Smart Parking System - Implementation Plan

This document outlines the implementation strategy for the Smart Parking System based on the provided Software Requirements Specification (SRS). The project will be developed in structured phases, ensuring stability and correct implementation of all requested features.

## User Review Required

Please review the proposed phases and the architecture below. Once you approve this plan, I will begin executing **Phase 1**.

## Open Questions

> [!IMPORTANT]  
> **Project Location:** Where would you like the `smart-parking-system` project directory to be created? By default, I will create it on your Desktop at `C:\Users\faraz\OneDrive\Desktop\smart-parking-system` (next to your current `sharkProj`). Let me know if you prefer a different location.

> [!WARNING]
> **MySQL Database:** For Phase 1, we will need a local MySQL server running. Please ensure you have MySQL installed and let me know the username/password and port (default is usually root/root or root/empty on 3306) so I can configure `application.properties` correctly. I will assume `root` with no password for now, but we can change it.

## Architecture & Tech Stack

- **Backend:** Spring Boot 3, Java 17+, Spring MVC, Spring Data JPA, Hibernate, Spring Security, JWT, Lombok.
- **Frontend:** Thymeleaf, HTML5, CSS3, Vanilla JS, Bootstrap 5.
- **Database:** MySQL.
- **Design Pattern:** Layered Architecture (Controller -> Service -> Repository -> Entity).
- **Security:** JWT Authentication, Role-based Access Control (ADMIN, USER), BCrypt Password Encryption.

## Proposed Phased Implementation

We will execute the project in the following strict phases, as requested:

### Phase 1: Foundation & Security (Immediate Next Step)
- Initialize Maven Project (`smart-parking-system`).
- Configure `pom.xml` dependencies (Spring Web, Data JPA, Security, Thymeleaf, MySQL Driver, Lombok, JWT).
- Establish project package structure (`controller`, `service`, `repository`, `entity`, `dto`, `security`, `config`, etc.).
- Configure `application.properties` for MySQL connection.
- Implement base Security Configuration and JWT Authentication (Filter, Util, EntryPoint).
- Run the application and verify startup.
- *Will pause for confirmation after this phase.*

### Phase 2: Domain Model & Database Design
- Create JPA Entities: `User`, `Vehicle`, `ParkingSlot`, `ParkingSession`, `Payment`, `WalletTransaction`.
- Map relationships (One-to-Many, One-to-One).
- Configure Hibernate to generate the database schema automatically (`update`).
- Run and verify database tables.

### Phase 3: Core Business Logic (Services)
- Create DTOs and MapStruct/Manual Mapper classes.
- Implement Global Exception Handling (`@ControllerAdvice`).
- Implement Services with validation and business logic (Slot assignment, billing calculation, due management).

### Phase 4: REST APIs & Controllers
- Expose RESTful endpoints for all modules.
- Secure endpoints with `@PreAuthorize`.
- Write initial Unit/Integration tests for Controllers and Services.

### Phase 5: Frontend Views (Thymeleaf & UI)
- Set up base Thymeleaf layouts and fragments (Navbar, Sidebar).
- Create custom CSS and JS.
- Build responsive UI components with Bootstrap 5 (White/Blue/Dark Gray theme).

### Phase 6 & 7: Dashboards, Billing, & Advanced Features
- Build Admin Dashboard (Charts, Reports, filtering).
- Build User Dashboard.
- Implement Wallet, Payments, and Due Management flows.

### Phase 8: Finalization
- Comprehensive Testing.
- Sample Data Generation (SQL script).
- Documentation (README, ER Diagram, Flowcharts).

## Verification Plan

For **Phase 1**, verification will consist of:
1. Running the `mvn clean install` command to ensure the project builds successfully.
2. Starting the Spring Boot application and ensuring there are no startup errors (specifically no DB connection errors or security context failures).
3. Verifying that the directory structure matches standard Spring Boot conventions.
