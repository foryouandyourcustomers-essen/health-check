package com.foryouandyourcustomers.health.test;

import static com.foryouandyourcustomers.health.HealthCheckType.LARGE;
import static com.foryouandyourcustomers.health.HealthCheckType.MEDIUM;
import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;
import static com.foryouandyourcustomers.health.HealthCheckType.SHORT;

import java.util.Map;
import com.foryouandyourcustomers.health.HealthCheck;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

@Component
public class HealthTest {

  private Map<String, Boolean> ranTest = Maps.newHashMap();

  @HealthCheck(type = ONCE)
  public void onceCheck() {
    this.ranTest.put(ONCE.toString(), true);
  }

  @HealthCheck(type = SHORT)
  public void shortCheck() {
    this.ranTest.put(SHORT.toString(), true);
  }

  @HealthCheck(type = MEDIUM)
  public void mediumCheck() {
    this.ranTest.put(MEDIUM.toString(), true);
  }

  @HealthCheck(type = LARGE)
  public void largeCheck() {
    this.ranTest.put(LARGE.toString(), true);
  }

  @HealthCheck
  public void defaultCheck() {
    this.ranTest.put("default", true);
  }

  public Map<String, Boolean> getRanTest() {
    return ranTest;
  }

}
