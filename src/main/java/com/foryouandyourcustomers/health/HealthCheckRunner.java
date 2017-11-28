package com.foryouandyourcustomers.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan("com.foryouandyourcustomers.health")
@EnableScheduling
public class HealthCheckRunner {

  public static void main(String[] args) {
    SpringApplication.run(HealthCheckRunner.class, args);
  }
}
