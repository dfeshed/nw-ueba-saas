package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class DataPullingConfiguration {

    @JsonProperty("source")
    private String source;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPullingConfiguration that = (DataPullingConfiguration) o;
        return source == that.source;
    }

    @Override
    public int hashCode() {

        return Objects.hash(source);
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
