# Health-check

## Motivation

Normal health-check frameworks allow you to add health checks that will be executed on the moment you call the health endpoint. If you want to add complex or time-costly operations like having checking that third parties software are available and healthy, you do not want to do this at the moment of calling the `/health` endpoint, because it may take too long to answer, it may be costly (operationally talking), it can add extra load to your application each time you check the `/health` endpoint, etc...

Therefore, this framework helps you by running the health checks in background and collecting the results, that afterwards are listed on in the `/health` endpoint of your application. At the same time, one of the main goals of this module is to enable the creation of health checks as a tool for testing the application in run time.

### Why would someone want to test applications in run time? 

The idea behind this is to have a framework that allows you to make custom health checks that can be a bit more complex than "just checking" if the connection is still alive with another services. This checks can be scheduled and run in the background of your application.

### When will you use this framework? 

Let's say that you have a third party system that your application/service uses and is vital for the normal functioning of your application/service. In this case, you may want to know more than "the service is reachable". You may want to know that the service provides you with the interface needed and that it answer in a certain time-span that you can find acceptable for the normal functioning of your application. 

## Description

This module contains the class HealthIndicator, that is responsible for running the health checks. There are four types of health checks availables:

* Health checks that only run once. This health checks run as soon as the context is started up.
* Health checks that continuously separated by a short time period (default 60000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.short"}` time.
* Health checks that continuously separated by a medium time period (default 1200000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.medium"}` time.
* Health checks that continuously separated by a large time period (default 36000000ms). This health check run for the first time as soon as the context is started up and then each `${"health.check.schedule.large"}` time.

## Properties

The following properties should be set up:

```
health.check.basepackage=BASE_PACKAGE #default vaule is "", meaning, all will be scanned 
health.check.schedule.short=60000 # default
health.check.schedule.medium=1200000 # default
health.check.schedule.large=36000000 # default
endpoints.health.sensitive=false # default (this configuration is only needed to see the json in a "nice" way when calling the /health endpoint)
management.security.enabled=false # default (this configuration is only needed to see the json in a "nice" way when calling the /health endpoint)
```

### Definition of properties:

The `health.check.basepackage` defines the base package in which the annotation `@HealthCheck` will be scanned for.

## Usage

Import the dependency in maven 

```
<dependency>
    <groupId>com.foryouandyourcustomers</groupId>
    <artifactId>health-check</artifactId>
    <version>1.0.1</version>
</dependency>
``` 

and add the properties to your property file. At the moment the software is still not published to the maven repository so, for using it you have to clone the repo and run `mvn install`.

In your bean, annotate the health checks methods with the annotation `@HealthCheck`. It will run automatically.

If your method returns something that is Serializable, then the result of that will be printed in the health endpoint under the description of the last run of the check.

In your runnable class/configuration class add the annotation `@EnableHealthCheck`.

Example:
```
@SpringBootApplication
@EnableHealthCheck
public class HealthCheckRunner {
  public static void main(String[] args) {
    SpringApplication.run(HealthCheckRunner.class, args);
  }
}

```

### Test

There are two ways of testing:

1. By running the test with maven.
2. Starting up the application and going to `localhost:8080/health`. This should show a json with the status of the application, including the extra health information.

### Troubleshooting

It may happen that, if you have a `@ComponentScan` definition in your configuration set up to your package, then the package `com.foryouandyourcustomers.health` is not scanned and therefore, the check will not appear. Just add that package to your component scan definition.


