Supermarket Management System - Employee Module and Integration
Project Overview
This project represents a comprehensive Supermarket Management System built as an integrated suite of four core modules: Employee Management, Shift Scheduling, Inventory, and Sales/Delivery. Each module handles a specific domain, working together to support full retail operations.

My main responsibility is the Employee Module, which includes the design and implementation of employee lifecycle management, roles, permissions, scheduling constraints, and business logic. Another critical part of my work is the integration layer that connects all four modules, ensuring seamless communication, data consistency, and unified user navigation.

Integration is implemented through a menu-driven interface supporting both a GUI and terminal interface. The terminal accepts arguments to navigate menus and operations, coordinating with GUI to provide a flexible user experience for both scriptable and graphical interaction.

Object-Oriented Modeling
Entities are modeled as discrete objects representing domain concepts such as Employees, Shifts, Roles, Constraints, Locations, etc. This clear separation allows for encapsulated, maintainable, and extensible code.

Data Transfer Objects (DTOs) enable clean communication between layers, and Mapper classes translate objects to/from database records. Facades wrap complex subsystems, providing simplified APIs for use by presentation layers.

Layered Architecture
The system is organized into these layers for clear separation of concerns:

Presentation Layer:

GUI implemented with Java Swing for interactive management.

Terminal interface parses arguments to drive operations and navigation.

Business Logic Layer:

Core business rules on employee management, scheduling, constraints, and access control encapsulated by Services and Facades.

Data Access Layer:

DAOs and Repository implementations manage database interactions through JDBC.

Mappers provide object-relational mapping.

Database Layer:

Relational database storing all persistent data.

Documentation and Instructions
Detailed documentation diagrams outlining the layered system architecture and component interactions were created using draw.io tools and are located in the docs folder.

Additionally, a structured menu and comprehensive operational instructions are provided in the INSTRUCTIONS folder inside docs. These text files guide users on how to run the system from terminal commands and GUI, ensuring smooth setup and usage.

Running the Project
Prerequisites
Java Development Kit (JDK) 11 or higher

A properly configured relational database compatible with the project's DAO schemas

Java build tools (Maven, Gradle) or an IDE capable of compiling and running Java applications

Running from Terminal (CLI)
You can run the system from the terminal with the GUI or CLI menu options. The terminal accepts arguments that enable you to navigate directly to specific modules or menus.

There are two main CLI options:

Option 1: Launch with specific module menu

bash
java -jar Supermarket.jar CLI StoreManager/HRManager/...
This runs the terminal interface starting directly at the specified module menu (e.g., StoreManager or HRManager).

Option 2: Launch default CLI menu

bash
java -jar Supermarket.jar CLI
This launches a general CLI menu from which you can navigate to all available modules and submenus.

Both options allow scripted or automated interactions without starting the GUI.

Running the GUI Interface
You can launch the GUI either from the terminal or directly from an IDE.

Option 1: Launch GUI with specific module menu via terminal

bash
java -jar Supermarket.jar GUI StoreManager/HRManager/...
Opens the GUI starting directly at the specified module's menu.

Option 2: Launch default GUI menu via terminal

bash
java -jar Supermarket.jar GUI
Starts the overall GUI main menu window, from which you can navigate into any module.

Option 3: Launch GUI directly from IDE
Run the main GUI entry class, such as LoginFrame or MainFrame. After login, navigate through menus for employees, shifts, roles, and constraints with full backend integration.

Notes on Menu Structure
The system supports opening either a global main menu containing all modules or individual module menus independently, enabling flexible user navigation.

The terminal and GUI interfaces are coordinated, reflecting the same menu options and system state for a seamless user experience.
