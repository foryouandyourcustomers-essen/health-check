package com.foryouandyourcustomers.health;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthCheckConfiguration {
  @Bean
  public CustomHealthIndicator customHealthIndicator(){
    return new CustomHealthIndicator();
  }
}
