package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets AlertSeverity
 */
public enum AlertSeverity {
  
  CRITICAL("CRITICAL"),
  
  HIGH("HIGH"),
  
  MEDIUM("MEDIUM"),
  
  LOW("LOW");

  private String value;

  AlertSeverity(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static AlertSeverity fromValue(String text) {
    for (AlertSeverity b : AlertSeverity.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

