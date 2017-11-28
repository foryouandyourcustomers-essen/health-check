package com.foryouandyourcustomers.health;

import static com.foryouandyourcustomers.health.HealthCheckType.LARGE;
import static com.foryouandyourcustomers.health.HealthCheckType.MEDIUM;
import static com.foryouandyourcustomers.health.HealthCheckType.ONCE;
import static com.foryouandyourcustomers.health.HealthCheckType.SHORT;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
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
public class FyaycHealthIndicator
    implements HealthIndicator,
        ApplicationContextAware,
        ApplicationListener<ContextRefreshedEvent> {

  private ConcurrentMap<String, Boolean> healthStatus;
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

    if (healthStatus.values().contains(false)) {
      return builder.down().build();
    }

    return builder.up().build();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
    this.healthStatus = Maps.newConcurrentMap();

    Reflections reflections =
        new Reflections(
            new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(healthCheckPackage))
                .setScanners(new MethodAnnotationsScanner()));
    Set<Method> methods = reflections.getMethodsAnnotatedWith(HealthCheck.class);

    methods.forEach(m -> healthStatus.put(m.getName(), false));

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
        m -> {
          try {
            m.invoke(applicationContext.getBean(m.getDeclaringClass()));
            healthStatus.put(m.getName(), true);
          } catch (Exception e) {
            healthStatus.put(m.getName(), false);
          }
        });
  }

  private List<Method> getMethods(Set<Method> methods, HealthCheckType type) {
    return methods
        .stream()
        .filter(m -> m.getAnnotation(HealthCheck.class).type().equals(type))
        .collect(Collectors.toList());
  }
}