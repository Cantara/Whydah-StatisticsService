Whydah StatisticsService 
========================


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
java -jar target/Whydah-....jar
```

## Verify setup

http://localhost:4901/reporter/