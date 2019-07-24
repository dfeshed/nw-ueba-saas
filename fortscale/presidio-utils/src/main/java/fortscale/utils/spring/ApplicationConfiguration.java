package fortscale.utils.spring;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySources;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ApplicationConfiguration implements ResourceLoaderAware {

    private ResourceLoader resourceLoader = new DefaultResourceLoader();

    protected <T> T bindPropertiesToTarget(Class<T> clazz, String prefix, String... locations) {
        try {

            Binder binder = new Binder(ConfigurationPropertySources.from(loadPropertySources(locations)));
            return binder.bind(prefix, clazz).get();

        } catch (Exception ex) {
            String targetClass = ClassUtils.getShortName(clazz);
            throw new BeanCreationException(clazz.getSimpleName(), "Could not bind properties to " + targetClass + " (" + clazz.getSimpleName() + ")", ex);
        }
    }

    private PropertySources loadPropertySources(String[] locations) {
        try {
            List<PropertySourceLoader> propertySourceLoaders = SpringFactoriesLoader.loadFactories(
                    PropertySourceLoader.class, getClass().getClassLoader());

            MutablePropertySources propertySources = new MutablePropertySources();
            for (String location : locations) {

                for (PropertySourceLoader loader : propertySourceLoaders) {
                    if (canLoadFileExtension(loader, location)) {
                        Resource resource = this.resourceLoader.getResource(location);
                        String name = "applicationConfig: [" + location + "]";
                        loader.load(name, resource).forEach(propertySources::addFirst);
                    }
                }


            }
            return propertySources;
        }
        catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
        return Arrays.stream(loader.getFileExtensions())
                .anyMatch((fileExtension) -> org.springframework.util.StringUtils.endsWithIgnoreCase(name,
                        fileExtension));
    }


    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
