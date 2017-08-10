package fortscale.resource;

import org.springframework.cloud.config.server.resource.ResourceRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(method = RequestMethod.PUT, path = "${spring.cloud.config.server.prefix:}")
public class ResourceWriteController {

    private ResourceWritableRepository resourceWritableRepository;

    private UrlPathHelper helper = new UrlPathHelper();

    public ResourceWriteController(ResourceWritableRepository resourceWritableRepository) {
        this.resourceWritableRepository = resourceWritableRepository;
        this.helper.setAlwaysUseFullPath(true);
    }

//    @RequestMapping("/{name}/{profile}")
//    public String postConfiguration(@PathVariable String name,@PathVariable String profile, HttpServletRequest request) {
//        try {
//            return resourceWritableRepository.store(name, null, null,true,"aaa");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }



    @RequestMapping(value="/{name}", method = RequestMethod.POST)
    public String postConfiguration(@PathVariable String name, HttpServletRequest request,
                           @RequestParam(defaultValue = "true") boolean resolvePlaceholders, @RequestBody String properties)
            throws IOException {
        return resourceWritableRepository.store(name, null, null,true, properties);
//        String path = getFilePath(request, name, profile, label);
//        return resourceWritableRepository.store(name, profile, label, path,true,"aaa");
    }


}
