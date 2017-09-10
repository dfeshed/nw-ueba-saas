package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonValue;

import com.fasterxml.jackson.annotation.JsonCreator;

/**
 * Gets or Sets Feedback
 */
public enum Feedback {
  
  NONE("None"),
  
  APPROVED("Approved"),
  
  REJECTED("Rejected");

  private String value;

  Feedback(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Feedback fromValue(String text) {
    for (Feedback b : Feedback.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

