package com.foryouandyourcustomers.health;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"com.foryouandyourcustomers.health"})
@EnableScheduling
public class HealthCheckRunner {
  public static void main(String[] args) {
    SpringApplication.run(HealthCheckRunner.class, args);
  }
}
