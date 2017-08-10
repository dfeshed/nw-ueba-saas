package fortscale.resource;


import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StringUtils;

import java.io.*;

public class GenericResourceWritableRepository implements ResourceWritableRepository, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private SearchPathLocator service;

    public GenericResourceWritableRepository(SearchPathLocator service) {
        this.service = service;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public String store(String application, String profile, String label, boolean override, String content) throws IOException{

        String location = getLocation(application, profile, label);
        String local = getPropertyPath(application, profile, "property");
        Resource resource = new FileSystemResource(this.resourceLoader.getResource(location).createRelative(local).getFile());
        if (!WritableResource.class.isInstance(resource)) {
            System.err.println(String.format("resource is not writable %s/%s", location, local));
            //TODO: error handling
        }

        WritableResource writableResource = (WritableResource) resource;
        try (OutputStream outputStream = writableResource.getOutputStream();
             Writer out = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF8"))) {
            out.append(content);
            out.flush();
        }

        return resource.getURI().getPath();
    }

    @Override
    public boolean add(String application, String profile, String label, String key, String value) {
        return false;
    }

    @Override
    public boolean remove(String application, String profile, String label, String key, String value) {
        return false;
    }


    protected String getLocation(String application, String profile, String label) {
        String[] locations = this.service.getLocations(application, profile, label).getLocations();
        String location = locations[0];
        return location;
    }

    protected String getPropertyPath(String application, String profile, String ext) {

        String path;
        if (StringUtils.hasText(profile) && !"default".equals(profile)) {
            path = String.format("%s-%s.%s", application, profile, ext);
        } else {
            path = String.format("%s.%s", application,  ext);
        }
        return path;
    }



}
