package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 */
//TODO- use Schema.java values
public enum SchemasEnum {
  
  FILE("FILE"),
  
  ACTIVE_DIRECTORY("ACTIVE_DIRECTORY"),
  
  AUTHENTICATION("AUTHENTICATION");

  private String value;

  SchemasEnum(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static SchemasEnum fromValue(String text) {
    for (SchemasEnum b : SchemasEnum.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }
}

