# QREASP
## Quality, Release and Automation Support

---

## What is QREASP?

**QREASP (Quality, Release and Automation Support)** is a **unified Java-based automation framework** for **Web UI (Selenium)** and **REST API test automation**.

It is designed for **enterprise-grade quality assurance**, providing:
- Advanced Selenium WebDriver wrapper logic
- A template-driven API testing framework
- Native integrations with **Jira**, **TeamCity**, **Swagger/OpenAPI**, and **Stash**
- Gradle-based builds suitable for CI/CD pipelines

QREASP helps teams reduce fragmentation, improve automation stability, and produce reliable quality signals for release decisions.

---

## Why QREASP?

Many teams maintain multiple automation frameworks that are:
- Hard to maintain
- Inconsistent across projects
- Poorly integrated into release pipelines

QREASP provides a **single, consistent automation foundation** that enables:
- Unified UI and API automation
- Reusable, standardized patterns
- Enterprise traceability and reporting
- CI/CD-ready execution from day one

---

## Core Capabilities

### Web UI Automation (Selenium)
- Selenium WebDriver wrappers to reduce flakiness
- Centralized waits, retries, and error handling
- Page Object best practices
- Cleaner and more readable test code

---

### API Test Automation (Java-Based)
- Robust and flexible API automation framework
- Template-driven request and assertion model
- Consistent structure for functional and regression tests
- Swagger/OpenAPI-aligned test generation and validation
- Built and executed using **Gradle**

---

### Integrations

| Tool | Purpose |
|------|---------|
| Jira | Automatic creation and updates of issues based on test results |
| TeamCity | CI/CD execution and reporting |
| Swagger / OpenAPI | API contract validation and test generation |
| Stash | Version control for test scripts and related assets |

---

### Quality & Release Support
- CI-friendly execution model
- Environment-aware testing (INT, Staging, Prod)
- Rich build, commit, and environment metadata
- Designed to support quality gates and release validation workflows

---

## Design Principles

- Java-first and enterprise-ready
- Framework over ad-hoc scripts
- Reusable, extensible, and maintainable
- CI/CD native
- Minimal configuration with sensible defaults

---

## Getting Started

### 1. Clone the Repository
Clone the repository to your local machine.

---

### 2. Prerequisites
Ensure the following are installed:
- Java
- Gradle

---

### 3. Build the Project

```bash
cd qreasp
./gradlew clean build
