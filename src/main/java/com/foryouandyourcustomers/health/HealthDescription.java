package com.foryouandyourcustomers.health;

import java.io.IOException;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;

public class HealthDescription {

  private boolean up = false;
  private String methodName;
  private String status;
  private Date lastStart;
  private Long runDuration;
  private String description;

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getLastStart() {
    return lastStart;
  }

  public void setLastStart(Date lastStart) {
    this.lastStart = lastStart;
  }

  public Long getRunDuration() {
    return runDuration;
  }

  public void setRunDuration(Long runDuration) {
    this.runDuration = runDuration;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isUp() {
    return up;
  }

  public void setUp(boolean up) {
    this.up = up;
  }

  @Override
  public String toString() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch (IOException e) {
      return "Error while mapping description for method: " + this.methodName;
    }
  }
}
