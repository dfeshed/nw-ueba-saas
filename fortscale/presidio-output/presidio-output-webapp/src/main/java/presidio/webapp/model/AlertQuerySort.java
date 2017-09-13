package presidio.webapp.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * AlertQuerySort
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-09-11T19:21:46.635Z")

public class AlertQuerySort {
    /**
     * the direction of the sort
     */
    public enum DirectionEnum {
        ASC("asc"),

        DSC("dsc");

        private String value;

        DirectionEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static DirectionEnum fromValue(String text) {
            for (DirectionEnum b : DirectionEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("direction")
    private DirectionEnum direction = null;

    /**
     * the field names
     */
    public enum FieldNamesEnum {
        SCORE("score");

        private String value;

        FieldNamesEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static FieldNamesEnum fromValue(String text) {
            for (FieldNamesEnum b : FieldNamesEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("fieldNames")
    private FieldNamesEnum fieldNames = null;

    public AlertQuerySort direction(DirectionEnum direction) {
        this.direction = direction;
        return this;
    }

    /**
     * the direction of the sort
     *
     * @return direction
     **/
    @ApiModelProperty(value = "the direction of the sort")
    public DirectionEnum getDirection() {
        return direction;
    }

    public void setDirection(DirectionEnum direction) {
        this.direction = direction;
    }

    public AlertQuerySort fieldNames(FieldNamesEnum fieldNames) {
        this.fieldNames = fieldNames;
        return this;
    }

    /**
     * the field names
     *
     * @return fieldNames
     **/
    @ApiModelProperty(value = "the field names")
    public FieldNamesEnum getFieldNames() {
        return fieldNames;
    }

    public void setFieldNames(FieldNamesEnum fieldNames) {
        this.fieldNames = fieldNames;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AlertQuerySort alertQuerySort = (AlertQuerySort) o;
        return Objects.equals(this.direction, alertQuerySort.direction) &&
                Objects.equals(this.fieldNames, alertQuerySort.fieldNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(direction, fieldNames);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class AlertQuerySort {\n");

        sb.append("    direction: ").append(toIndentedString(direction)).append("\n");
        sb.append("    fieldNames: ").append(toIndentedString(fieldNames)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

