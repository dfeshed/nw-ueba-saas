package presidio.webapp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * A JSONPatch document as defined by RFC 6902 (http://jsonpatch.com/)
 */
@ApiModel(description = "A JSONPatch document as defined by RFC 6902 (http://jsonpatch.com/)")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-11-14T08:22:42.779Z")

public class PatchOperation {
    /**
     * The operation to be performed
     */
    public enum OpEnum {
        ADD("add"),

        REMOVE("remove"),

        REPLACE("replace");

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
    private List<String> value = new ArrayList<String>();

    public PatchOperation op(OpEnum op) {
        this.op = op;
        return this;
    }

    /**
     * The operation to be performed
     * @return op
     **/
    @ApiModelProperty(required = true, value = "The operation to be performed")
    public OpEnum getOp() {
        return op;
    }

    public void setOp(OpEnum op) {
        this.op = op;
    }

    public PatchOperation path(String path) {
        this.path = path;
        return this;
    }

    /**
     * Get path
     * @return path
     **/
    @ApiModelProperty(required = true, value = "")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public PatchOperation value(List<String> value) {
        this.value = value;
        return this;
    }

    public PatchOperation addValueItem(String valueItem) {
        this.value.add(valueItem);
        return this;
    }

    /**
     * Get value
     * @return value
     **/
    @ApiModelProperty(value = "")
    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
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
        PatchOperation patchRequestInner = (PatchOperation) o;
        return Objects.equals(this.op, patchRequestInner.op) &&
                Objects.equals(this.path, patchRequestInner.path) &&
                Objects.equals(this.value, patchRequestInner.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(op, path, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class PatchRequestInner {\n");

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

