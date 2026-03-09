# Whydah-StatisticsService

## Purpose
Reporting and statistics backend service for Whydah. Collects, stores, and serves usage statistics data about user activities, login events, and application usage within a Whydah installation.

## Tech Stack
- Language: Java 21
- Framework: Jersey, Jetty, Spring
- Build: Maven
- Key dependencies: Whydah-Java-SDK, Valuereporter, Constretto

## Architecture
Standalone microservice with embedded database support. Collects statistics from Whydah services, stores them in a relational database (embedded or external), and exposes REST APIs for querying statistics data. Pairs with Whydah-StatisticsDashboard for visualization.

## Key Entry Points
- `application_override.properties` - Configuration overrides
- REST API for statistics queries
- `pom.xml` - Maven coordinates: `net.whydah.service:Whydah-StatisticsService`

## Development
```bash
# Build
mvn package

# Setup (embedded database)
# Set jdbc.setupNewDb=true in application_override.properties

# Run
java -jar target/Whydah-StatisticsService-*.jar
```

## Domain Context
Whydah IAM analytics backend. Collects and persists usage statistics from the Whydah ecosystem, providing the data layer for the StatisticsDashboard and enabling operational reporting on user activity patterns.
