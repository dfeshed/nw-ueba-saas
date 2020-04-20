package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.annotation.Generated;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2019-04-15T00:00:00.000Z")
public class ConfigurationResponse {
    private static final Pattern jsonMappingExceptionMessagePattern = Pattern.compile("^.*problem: (.*)$");

    private String message;

    public ConfigurationResponse(String message, JsonMappingException e) {
        Validate.notBlank(message, "message cannot be blank");

        if (e != null) {
            List<String> fieldNames = e.getPath().stream().map(Reference::getFieldName).collect(Collectors.toList());
            fieldNames.add(0, "root");
            String location = fieldNames.stream().collect(Collectors.joining("/"));
            Matcher matcher = jsonMappingExceptionMessagePattern.matcher(e.getOriginalMessage());
            String reason = matcher.matches() ? matcher.group(1) : "Unknown";
            message = String.format("%s (location: %s, reason: %s)", message, location, reason);
        }

        this.message = message;
    }

    @JsonCreator
    public ConfigurationResponse(@JsonProperty("message") String message) {
        this(message, null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return getMessage();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof ConfigurationResponse)) return false;
        ConfigurationResponse that = (ConfigurationResponse)object;
        return new EqualsBuilder()
                .append(message, that.message)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(message)
                .toHashCode();
    }
}
