# QREASP - Quality, Release and Automation support library

🧪 API test automation framework (Java-based)
This framework provides a robust and flexible solution for automating API tests, written in Java. It leverages templating for easy test case creation and offers seamless integration with key development and project management tools using Gradle as its build system.
✨ Features
- Templating: Utilize templates for creating API test requests and assertions, simplifying test case development and promoting consistency.
- Jira Integration: Automate the creation and updating of Jira issues based on test results, improving issue tracking and collaboration.
- Stash Integration: (Assuming Stash for code management) Integrates with Stash for version control of test scripts and related files.
- TeamCity Integration: Easily integrate the framework with TeamCity for continuous integration and automated test execution within your build pipelines.
- Swagger Integration: Utilize Swagger definitions to generate or validate API requests and responses, streamlining API test creation and ensuring adherence to API specifications.

🚀 Getting started
- Clone the Repository: Clone this repository to your local machine.
- Prerequisites: Ensure you have Java and Gradle installed.
- Build the Project: Build the project using Gradle to download dependencies and compile the code.
  Run cd qreasp and execute the following command to build the project and execute tests
  - ./gradlew clean build
- Configure Integrations: Configure the framework with your Jira, Stash, and TeamCity instances as needed       (e.g., provide API credentials or configuration files).
- Create/Modify Tests: Create new API test cases using the provided templates and data files, or modify existing ones to suit your needs.
- Run Tests: Execute API tests locally via Gradle or integrate them with your TeamCity build configurations for automated runs.
