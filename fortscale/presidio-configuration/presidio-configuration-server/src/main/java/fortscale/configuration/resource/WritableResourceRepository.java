package fortscale.configuration.resource;

import org.springframework.core.io.InputStreamSource;

/**
 * Spring configuration server exposes the {@link ResourceRepository} class to <STRONG>read</STRONG>
 * a property file (Resource) from the configuration server.
 * This WritableResourceRepository adds the ability to <STRONG>write</STRONG> properties files to the configuration server.
 */
public interface WritableResourceRepository {

    void store (String application, String profile, String label, String extension, InputStreamSource resource);

    void update (String application, String profile, String label, String key, String value);

    void remove (String application, String profile, String label, String key, String value);

}
