package fortscale.resource;

import org.springframework.cloud.config.environment.PropertySource;

import java.io.IOException;

public interface ResourceWritableRepository  {

    public String store (String application, String profile, String label, boolean override, String content) throws IOException;
    public boolean add (String application, String profile, String label, String key, String value);
    public boolean remove (String application, String profile, String label, String key, String value);

}
