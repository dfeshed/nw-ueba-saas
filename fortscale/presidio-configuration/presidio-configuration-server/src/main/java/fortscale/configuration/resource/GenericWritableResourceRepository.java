package fortscale.configuration.resource;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.config.server.environment.RepositoryException;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.*;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * An {@link WritableResourceRepository} backed by a {@link SearchPathLocator}.
 */
public class GenericWritableResourceRepository implements WritableResourceRepository, ResourceLoaderAware {

    protected Log logger = LogFactory.getLog(getClass());

    @Value("${spring.cloud.config.server.defaultProfile:default}")
    private String defaultProfile;

    private ResourceLoader resourceLoader;

    private SearchPathLocator service;

    public GenericWritableResourceRepository(SearchPathLocator service) {
        this.service = service;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public synchronized void store(String application, String profile, String label, String extension, InputStreamSource inputResource) {

        String location = getLocation(application, profile, label);
        String fileName = getPropertyFileName(application, profile, extension);

        try {
            Resource outputResource = new FileSystemResource(this.resourceLoader.getResource(location).createRelative(fileName).getFile());
            WritableResource writableResource = (WritableResource) outputResource;
            StreamUtils.copy(inputResource.getInputStream(), writableResource.getOutputStream());
            logger.debug(String.format("File %s has been stored into the configuration server. path:%s", location, fileName));
        } catch (ClassCastException ex) {
            logger.error(String.format("Cannot store file %s/%s to the configuration server", location, fileName), ex);
            throw new IllegalStateException(String.format("Cannot store file %s/%s to the configuration server", location, fileName), ex);
        } catch (IOException ex) {
            logger.error(String.format("Failed to store file %s/%s to the configuration server", location, fileName), ex);
            throw new RepositoryException(String.format("failed to store file %s/%s to the configuration server", location, fileName), ex);
        }
    }

    @Override
    public void delete(String application, String profile, String label, String extension) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void update(String application, String profile, String label, String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove(String application, String profile, String label, String key, String value) {
        throw new UnsupportedOperationException();
    }


    protected String getLocation(String application, String profile, String label) {
        String[] locations = this.service.getLocations(application, profile, label).getLocations();
        if (locations.length == 0) {
            throw new IllegalStateException("The configuration is invalid. no location for configuration server files.");
        }
        String location = locations[0];
        return location;
    }

    protected String getPropertyFileName(String application, String profile, String ext) {

        String path;
        if (StringUtils.hasText(profile) && !defaultProfile.equals(profile)) {
            path = String.format("%s-%s.%s", application, profile, ext);
        } else {
            path = String.format("%s.%s", application,  ext);
        }
        return path;
    }



}
