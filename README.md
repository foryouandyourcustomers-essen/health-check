### Goal

The main goal of this module is to enable the creation of health checks as a tool for testing the application in run time.

### Description

This module contains the class FyaycHealthIndicator, that is responsible for running the health checks. There are four types of health checks availables:

* Health checks that only run once. This health checks run as soon as the context is started up.
* Health checks that continuously separated by a short time period (default 60000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.short"}` time.
* Health checks that continuously separated by a medium time period (default 1200000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.medium"}` time.
* Health checks that continuously separated by a large time period (default 36000000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.large"}` time.

### Properties

The following properties should be set up:

```
health.check.basepackage=BASE_PACKAGE #default vaule is "", meaning, all will be scanned 
health.check.schedule.short=60000 # default
health.check.schedule.medium=1200000 # default
health.check.schedule.large=36000000 # default
endpoints.health.sensitive=false # default
management.security.enabled=false # default
```

#### Definition of properties:

The `health.check.basepackage` defines the base package in which the annotation `@HealthCheck` will be scanned for.

### Usage

The way of using this software is rather straight forward. Import the dependency in maven 

```
<dependency>
    <groupId>com.foryouandyourcustomers</groupId>
    <artifactId>health-check</artifactId>
    <version>1.0</version>
</dependency>
``` 

and add the properties to your property file. At the moment the software is still not published to the maven repository so, for using it you have to clone the repo and run `mvn install`.


