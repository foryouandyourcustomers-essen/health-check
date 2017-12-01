package com.foryouandyourcustomers.health;

import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HealthCheck {

  /**
   * Defining the type of the health check allows you to define the period on which the health checks will be run.
   * @return the type of the Health check. By default ${HealthCheckTyp.ONCE}.
   */
  HealthCheckType type() default ONCE;
}
