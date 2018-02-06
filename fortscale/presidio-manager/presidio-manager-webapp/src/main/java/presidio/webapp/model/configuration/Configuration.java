package presidio.webapp.model.configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

/**
 * SecuredConfiguration
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-07T07:15:37.402Z")

public class Configuration {
    @JsonProperty("system")
    private SystemConfiguration system = null;

    @JsonProperty("dataPipeline")
    private DataConfiguration dataPipeline = null;

    @JsonProperty("outputForwarding")
    private OutputConfiguration outputForwarding = null;


    public Configuration outputForwarding(OutputConfiguration outputForwarding) {
        this.outputForwarding = outputForwarding;
        return this;
    }

    public OutputConfiguration getOutputForwarding() {
        return outputForwarding;
    }

    public void setOutputForwarding(OutputConfiguration outputForwarding) {
        this.outputForwarding = outputForwarding;
    }


    public Configuration system(SystemConfiguration system) {
        this.system = system;
        return this;
    }

    /**
     * Get system
     *
     * @return system
     **/
    @ApiModelProperty(value = "")
    public SystemConfiguration getSystem() {
        return system;
    }

    public void setSystem(SystemConfiguration system) {
        this.system = system;
    }

    public Configuration dataPipeline(DataConfiguration dataPipeline) {
        this.dataPipeline = dataPipeline;
        return this;
    }

    /**
     * Get dataPipeline
     *
     * @return dataPipeline
     **/
    @ApiModelProperty(value = "")
    public DataConfiguration getDataPipeline() {
        return dataPipeline;
    }

    public void setDataPipeline(DataConfiguration dataPipeline) {
        this.dataPipeline = dataPipeline;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Configuration _configuration = (Configuration) o;
        return Objects.equals(this.outputForwarding, _configuration.outputForwarding) &&
                Objects.equals(this.system, _configuration.system) &&
                Objects.equals(this.dataPipeline, _configuration.dataPipeline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(system, dataPipeline, outputForwarding);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SecuredConfiguration {\n");

        sb.append("    system: ").append(toIndentedString(system)).append("\n");
        sb.append("    outputForwarding: ").append(toIndentedString(outputForwarding)).append("\n");
        sb.append("    dataPipeline: ").append(toIndentedString(dataPipeline)).append("\n");
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

