package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets UserSeverityDomain
 */
public enum UserSeverity {
  
  CRITICAL("CRITICAL"),
  
  HIGH("HIGH"),
  
  MEDIUM("MEDIUM"),
  
  LOW("LOW");

  private String value;

  UserSeverity(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static UserSeverity fromValue(String text) {
    for (UserSeverity b : UserSeverity.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

