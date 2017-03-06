package fortscale.domain.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by alexp on 02/03/2017.
 */
@ApiModel()
public class SystemSetupFileConf {
    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, value = "The tagging file path")
    private String path;

    @NotNull
    @NotEmpty
    @ApiModelProperty(required = true, value = "The tagging file mode")
    private SystemSetupFileRunningMode mode;

    public SystemSetupFileConf() {
    }

    public SystemSetupFileConf(String path, SystemSetupFileRunningMode mode) {
        this.path = path;
        this.mode = mode;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public SystemSetupFileRunningMode getMode() {
        return mode;
    }

    public void setMode(SystemSetupFileRunningMode mode) {
        this.mode = mode;
    }
}
