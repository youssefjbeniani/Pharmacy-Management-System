# Pharmacy Management System

A desktop application for managing pharmacy operations, built with Java, SQLite, and JDBC. The system supports pharmacist and manager workflows for medicine records, inventory, sales, replenishment, and stock movement tracking.

## Features

- **Role-based access:** Authentication for pharmacists and managers.
- **Medicine management:** Add medicines, remove records, and update prices.
- **Inventory control:** Manage stock levels and monitor critical stock.
- **Sales management:** Register, view, and cancel sales.
- **Replenishment orders:** Manage orders for restocking medicines.
- **Stock movement history:** Review inventory changes over time.

## Technology Stack

- **Language:** Java
- **Database:** SQLite
- **Database connectivity:** JDBC
- **Architecture:** DAO, Service, and Application layers

## Architecture

The application separates persistence, business logic, and application entry points:

- **DAO layer** handles database access.
- **Service layer** contains business operations and rules.
- **Application layer** coordinates the system and its user workflows.

## Getting Started

### Prerequisites

- Java Development Kit (JDK)
- SQLite JDBC driver

Configure the SQLite database and make the JDBC driver available to the application before running it. Build and launch commands depend on the project configuration.

## Project Status

This project provides core workflows for pharmacy administration and inventory management. See the source code for implementation details and current capabilities.
