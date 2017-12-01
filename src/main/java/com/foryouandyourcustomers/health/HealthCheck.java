package com.foryouandyourcustomers.health;

import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface HealthCheck {
  HealthCheckType type() default ONCE;
}
