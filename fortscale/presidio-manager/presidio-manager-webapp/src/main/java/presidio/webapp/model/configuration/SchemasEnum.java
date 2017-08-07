package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 */
public enum SchemasEnum {
  
  FILE("file"),
  
  ACTIVE_DIRECTORY("active directory"),
  
  AUTHENTICATION("authentication");

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

