Whydah StatisticsService 
========================


![Build Status](https://jenkins.capraconsulting.no/buildStatus/icon?job=Whydah-StatisticsService) [![Project Status: Active – The project has reached a stable, usable state and is being actively developed.](http://www.repostatus.org/badges/latest/active.svg)](http://www.repostatus.org/#active)  

[![Known Vulnerabilities](https://snyk.io/test/github/Cantara/Whydah-StatisticsService/badge.svg)](https://snyk.io/test/github/Cantara/Whydah-StatisticsService)

A reporting and statistics module for Whydah.


For more info, see:
 * https://wiki.cantara.no/display/whydah/Statistics+Service
 
 ## Initial Setup
 
 ```
 mvn package
 ```
 ### Use embedded database
 
 1. Create application_override.properties (use application_override.properties.example)
 2. Uncomment 
  ```
  jdbc.setupNewDb=true
   ```
 
### Start service

```
java -jar target/Whydah-StatisticsService....jar
```

## Verify setup

http://localhost:4901/reporter/
