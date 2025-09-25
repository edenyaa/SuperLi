Supermarket Management System
Employee Module & Integration Project Overview

This project represents a comprehensive Supermarket Management System built as an integrated suite of four core modules:

Employee Management

Shift Scheduling

Inventory

Sales/Delivery

Each module handles a specific domain, working together to support full retail operations.

My Responsibilities

My primary responsibility is the Employee Module, which includes:

Employee lifecycle management

Roles and permissions

Scheduling constraints

Core business logic

I also developed the integration layer that connects all four modules, ensuring:

Seamless communication between modules

Data consistency across the system

Unified user navigation via both CLI and GUI

Architecture & Design
Object-Oriented Modeling

Entities modeled as discrete objects: Employees, Shifts, Roles, Constraints, Locations

Provides encapsulation, maintainability, and extensibility

Data Transfer Objects and Mappers

DTOs for communication between layers

Mapper classes for object-relational translation

Facades

Simplified APIs wrapping complex subsystems

Used by presentation layers for easy interaction

Layered Architecture
Presentation Layer

GUI: Implemented with Java Swing for interactive management

CLI: Terminal interface parses arguments for operations and navigation

Business Logic Layer

Core rules for employee management, scheduling, constraints, and access control

Encapsulated in services and facades

Data Access Layer

DAOs and repositories manage database interactions via JDBC

Mappers provide object-relational mapping

Database Layer

Relational database storing all persistent data

Documentation

System architecture diagrams created with draw.io, located in /docs

Operational instructions available in /docs/INSTRUCTIONS

Includes guides for running the system via CLI and GUI

Running the Project
Prerequisites

Java Development Kit (JDK 11 or higher)

Configured relational database compatible with the project schemas

Java build tools (Maven, Gradle) or IDE (e.g., IntelliJ, Eclipse)

Running from Terminal (CLI)

There are two main options:

Option 1: Launch with a specific module menu
java -jar Supermarket.jar CLI StoreManager/HRManager/...


Starts directly in the specified module menu (e.g., StoreManager, HRManager).

Option 2: Launch default CLI menu
java -jar Supermarket.jar CLI


Opens the general CLI menu, allowing navigation to all modules.

Running the GUI
Option 1: Launch GUI with a specific module menu
java -jar Supermarket.jar GUI StoreManager/HRManager/...

Option 2: Launch default GUI menu
java -jar Supermarket.jar GUI

Option 3: Launch GUI directly from IDE

Run the main GUI entry class (LoginFrame or MainFrame).
After login, navigate through menus for employees, shifts, roles, and constraints with full backend integration.

Menu Structure

The system supports:

Opening a global main menu containing all modules

Opening individual module menus independently

Both CLI and GUI reflect the same menu options and system state for a consistent user experience
