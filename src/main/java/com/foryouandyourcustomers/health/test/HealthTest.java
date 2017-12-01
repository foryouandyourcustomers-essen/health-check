package com.foryouandyourcustomers.health.test;

import static com.foryouandyourcustomers.health.HealthCheckType.LARGE;
import static com.foryouandyourcustomers.health.HealthCheckType.MEDIUM;
import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;
import static com.foryouandyourcustomers.health.HealthCheckType.SHORT;

import java.util.HashMap;
import java.util.Map;
import com.foryouandyourcustomers.health.HealthCheck;
import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;

@Component
public class HealthTest {

  private Map<String, Boolean> ranTest = Maps.newHashMap();

  public Map<String, Boolean> getRanTest() {
    return ranTest;
  }

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

  @HealthCheck
  public Map<String, String> exampleWithReturn() {
    HashMap<String, String> description = new HashMap<String, String>();
    description.put(
        "Adding any serializable object here", "will produce extra description in the health json");
    this.ranTest.put("exampleWithReturn", true);
    return description;
  }
}
