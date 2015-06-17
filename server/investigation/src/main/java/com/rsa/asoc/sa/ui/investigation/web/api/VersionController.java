package com.rsa.asoc.sa.ui.investigation.web.api;

import com.rsa.asoc.sa.ui.common.BuildInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller to return the API version information
 *
 * @author athielke
 * @since 11.0.0.0
 */
@RestController
public class VersionController {

    private final BuildInformation buildInformation;

    @Autowired
    public VersionController(BuildInformation buildInformation) {
        this.buildInformation = buildInformation;
    }

    @RequestMapping("/api/info")
    public BuildInformation getBuildInfo() {
        return buildInformation;
    }
}
