package com.foryouandyourcustomers.health;

import static com.foryouandyourcustomers.health.HealthCheckType.LARGE;
import static com.foryouandyourcustomers.health.HealthCheckType.MEDIUM;
import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;
import static com.foryouandyourcustomers.health.HealthCheckType.SHORT;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CustomHealthIndicator
    implements HealthIndicator,
        ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

  private ConcurrentMap<String, HealthDescription> healthStatus;
  private List<Method> runOnlyOnceMethods;
  private List<Method> shortScheduledMethods;
  private List<Method> mediumScheduledMethods;
  private List<Method> largeScheduledMethods;
  private ApplicationContext applicationContext;

  @Value("${health.check.basepackage}")
  public String healthCheckPackage;

  @Override
  public Health health() {

    Health.Builder builder = new Health.Builder();

    this.healthStatus.keySet().forEach(key -> builder.withDetail(key, this.healthStatus.get(key)));

    HealthDescription other = new HealthDescription();
    other.setUp(true);
    HealthDescription description =
        healthStatus.values().stream().filter(v -> !v.isUp()).findFirst().orElse(other);

    if (!description.isUp()) {
      return builder.down().build();
    }

    return builder.up().build();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.healthStatus = Maps.newConcurrentMap();

    if (Strings.isNullOrEmpty(healthCheckPackage)) {
      healthCheckPackage = "";
    }

    Reflections reflections =
        new Reflections(
            new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(healthCheckPackage))
                .setScanners(new MethodAnnotationsScanner()));
    Set<Method> methods = reflections.getMethodsAnnotatedWith(HealthCheck.class);

    methods.forEach(
        method -> {
          HealthDescription description = new HealthDescription();
          description.setMethodName(method.getName());
          healthStatus.put(method.getName(), description);
        });

    runOnlyOnceMethods = getMethods(methods, ONCE);
    shortScheduledMethods = getMethods(methods, SHORT);
    mediumScheduledMethods = getMethods(methods, MEDIUM);
    largeScheduledMethods = getMethods(methods, LARGE);
  }

  @Scheduled(fixedDelayString = "${health.check.schedule.short}")
  public void runShortScheduledCheckTests() {
    executeMethods(shortScheduledMethods);
  }

  @Scheduled(fixedDelayString = "${health.check.schedule.medium}")
  public void runMediumScheduledCheckTests() {
    executeMethods(mediumScheduledMethods);
  }

  @Scheduled(fixedDelayString = "${health.check.schedule.large}")
  public void runLargeScheduledCheckTests() {
    executeMethods(largeScheduledMethods);
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    executeMethods(runOnlyOnceMethods);
    runShortScheduledCheckTests();
    runMediumScheduledCheckTests();
    runLargeScheduledCheckTests();
  }

  private void executeMethods(List<Method> methods) {
    methods.forEach(
        method -> {
          HealthDescription healthDescription = healthStatus.get(method.getName());
          healthDescription.setLastStart(new Date());
          try {
            Object userData = method.invoke(applicationContext.getBean(method.getDeclaringClass()));
            String description =
                userData != null ? new ObjectMapper().writeValueAsString(userData) : "";
            healthStatus.put(
                method.getName(), setDescription(healthDescription, true, "UP", description));
          } catch (Exception e) {
            healthStatus.put(
                method.getName(),
                setDescription(healthDescription, false, "DOWN", e.getCause().getMessage()));
          }
        });
  }

  private HealthDescription setDescription(
      HealthDescription healthDescription, boolean up, String status, String description) {
    healthDescription.setUp(up);
    Date lastStart = healthDescription.getLastStart();
    healthDescription.setRunDuration(new Date().getTime() - lastStart.getTime());
    healthDescription.setStatus(status);
    healthDescription.setDescription(description);
    return healthDescription;
  }

  private List<Method> getMethods(Set<Method> methods, HealthCheckType type) {
    return methods
        .stream()
        .filter(method -> method.getAnnotation(HealthCheck.class).type().equals(type))
        .collect(Collectors.toList());
  }
}
