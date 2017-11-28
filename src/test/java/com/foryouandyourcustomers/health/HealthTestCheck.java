package com.foryouandyourcustomers.health;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Map;
import com.foryouandyourcustomers.health.test.HealthTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HealthTestCheck {

  @Autowired public HealthTest healthTest;

  @Value("${health.check.schedule.large}")
  public Long large;

  @Test
  public void healthCheckTest() {
    long now = System.currentTimeMillis();
    long after = System.currentTimeMillis();
    boolean finished = false;

    // we give it 1 sec chance to finish.
    while (((after - now) - 1000) < large && !finished) {
      Map<String, Boolean> ranTest = healthTest.getRanTest();
      finished = !ranTest.values().contains(false);
      after = System.currentTimeMillis();
    }
    assertThat("Health check did not run: " + healthTest.getRanTest(), finished, is(true));
  }
}
