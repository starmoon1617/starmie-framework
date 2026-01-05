# Copilot Instructions for Starmie Framework

## Project Overview
Starmie Framework is a modular Java project designed to provide common functionalities, utilities, and code generation tools. It is built with Maven and supports modern Java versions.

## Technology Stack
- **Java**: Version 25
- **Build Tool**: Maven
- **Frameworks**: 
  - Spring Boot 4.0.0
  - Spring Framework 7.0.2
  - MyBatis 3.5.19
- **Utilities**:
  - Apache POI 5.5.1 (Excel handling)
  - Apache PDFBox 3.0.6 (PDF handling)
  - Freemarker 2.3.34 & Thymeleaf 3.1.3.RELEASE (Templating)

## Module Structure

### 1. starmie-core
Base definitions and common utilities.
- **starmie-core-base**: Base model, mapper, service, and manager definitions.
- **starmie-core-common**: Common utility classes and custom query criteria builders.
- **starmie-core-service**: Service layer abstractions.

### 2. starmie-utils
Document processing utilities.
- **starmie-utils-poi**: Excel import/export utilities.
- **starmie-utils-pdf**: PDF export utilities.
- **starmie-utils-doc**: General document utilities.

### 3. starmie-boot
Spring Boot integration and auto-configuration.
- **starmie-boot-executor**: Thread pool auto-configuration.
- **starmie-boot-banner**: Custom startup banner configuration.

### 4. starmie-app
Web and Service layer tools.
- Provides generic CRUD definitions and implementations for web applications.

### 5. starmie-generator
Code generation tools based on MyBatis Generator.
- **starmie-generator-core**: Core generation logic.
- **starmie-generator-freemarker**: Support for Freemarker templates.
- **starmie-generator-thymeleaf**: Support for Thymeleaf templates.
- Capable of generating models, mappers, services, managers, controllers, and frontend files (JS/HTML).

## Development Guidelines
- **Code Style**: Follow standard Java conventions.
- **Generics**: The core modules rely heavily on generics for base classes (Model, Mapper, Service).
- **Configuration**: Spring Boot auto-configuration is used in `starmie-boot`.
