package fortscale.configuration.resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(method = RequestMethod.PUT, path = "${spring.cloud.config.server.prefix:}")
public class WritableResourceController {

    @Value("${spring.cloud.config.server.defaultProfile:default}")
    private String defaultProfile;

    @Value("${spring.cloud.config.server.defaultLabel:master}")
    private String defaultLabel;

    private WritableResourceRepository resourceWritableRepository;

    private UrlPathHelper helper = new UrlPathHelper();

    public WritableResourceController(WritableResourceRepository resourceWritableRepository) {
        this.resourceWritableRepository = resourceWritableRepository;
        this.helper.setAlwaysUseFullPath(true);
    }

    @RequestMapping(value="/{name}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void putJsonConfiguration(@PathVariable String name, HttpServletRequest request, @RequestBody Resource jsonResource) throws IOException {
        resourceWritableRepository.store(name, defaultProfile, defaultLabel, "json", jsonResource);
    }

    @RequestMapping(value="/{name}", method = RequestMethod.PUT, consumes = "application/x-yaml")
    @ResponseStatus(HttpStatus.CREATED)
    public void putYamlConfiguration(@PathVariable String name, HttpServletRequest request, @RequestBody Resource yamlResource) throws IOException {
        resourceWritableRepository.store(name, defaultProfile, defaultLabel, "yaml", yamlResource);
    }

    @RequestMapping(value="/{name}", method = RequestMethod.PUT, consumes = "application/x-java-properties")
    @ResponseStatus(HttpStatus.CREATED)
    public void putPropertiesConfiguration(@PathVariable String name, HttpServletRequest request, @RequestBody Resource propertiesResource) throws IOException {
        resourceWritableRepository.store(name, defaultProfile, defaultLabel, "properties", propertiesResource);
    }
}
