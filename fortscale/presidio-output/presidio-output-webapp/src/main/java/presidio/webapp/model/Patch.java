package presidio.webapp.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * Patch
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class Patch {
    /**
     * Operation - replace value, add to list of values, remove from list of values
     */
    public enum OpEnum {
        REPLACE("replace"),

        ADD("add"),

        REMOVE("remove");

        private String value;

        OpEnum(String value) {
            this.value = value;
        }

        @Override
        @JsonValue
        public String toString() {
            return String.valueOf(value);
        }

        @JsonCreator
        public static OpEnum fromValue(String text) {
            for (OpEnum b : OpEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }
    }

    @JsonProperty("op")
    private OpEnum op = null;

    @JsonProperty("path")
    private String path = null;

    @JsonProperty("value")
    private Object value = null;

    public Patch op(OpEnum op) {
        this.op = op;
        return this;
    }

    /**
     * Operation - replace value, add to list of values, remove from list of values
     *
     * @return op
     **/
    @ApiModelProperty(required = true, value = "Operation - replace value, add to list of values, remove from list of values")
    public OpEnum getOp() {
        return op;
    }

    public void setOp(OpEnum op) {
        this.op = op;
    }

    public Patch path(String path) {
        this.path = path;
        return this;
    }

    /**
     * The path to the field, I.E. /feedback, /tags, /watched
     *
     * @return path
     **/
    @ApiModelProperty(required = true, value = "The path to the field, I.E. /feedback, /tags, /watched")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Patch value(Object value) {
        this.value = value;
        return this;
    }

    /**
     * The new value
     *
     * @return value
     **/
    @ApiModelProperty(required = true, value = "The new value")
    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }


    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Patch patch = (Patch) o;
        return Objects.equals(this.op, patch.op) &&
                Objects.equals(this.path, patch.path) &&
                Objects.equals(this.value, patch.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, path, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Patch {\n");

        sb.append("    op: ").append(toIndentedString(op)).append("\n");
        sb.append("    path: ").append(toIndentedString(path)).append("\n");
        sb.append("    value: ").append(toIndentedString(value)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

