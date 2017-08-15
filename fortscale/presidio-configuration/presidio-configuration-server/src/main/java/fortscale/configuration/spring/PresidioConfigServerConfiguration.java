package fortscale.configuration.spring;

import fortscale.configuration.resource.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.cloud.config.server.environment.JGitEnvironmentRepository;
import org.springframework.cloud.config.server.environment.SearchPathLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@Configuration
public class PresidioConfigServerConfiguration {

    @Configuration
    @ConditionalOnMissingBean(WritableResourceRepository.class)
    protected static class DefaultWritableRepositoryConfiguration {

        @Autowired
        ResourceLoader resourceLoader;

        @Autowired
        SearchPathLocator service;

        @Autowired
        JGitEnvironmentRepository gitAccessor;

        @Bean
        public JGitWritableResourceRepository defaultResourceWritableRepository(SearchPathLocator service, JGitEnvironmentRepository gitAccessor) {
            JGitWritableResourceRepository repository = new JGitWritableResourceRepository(service, gitAccessor);
            return repository;
        }
    }

    @Configuration
    @Profile("native")
    protected static class NativeResourceWritableRepository {

        @Autowired
        ResourceLoader resourceLoader;

        @Autowired
        SearchPathLocator service;

        @Bean
        public WritableResourceRepository nativeResourceWritableRepository(SearchPathLocator service) {
            return new GenericWritableResourceRepository(service);
        }
    }

    @Configuration
    @Profile("git")
    protected static class GitRepositoryConfiguration extends DefaultWritableRepositoryConfiguration {}


    @Bean
    public ResponseBodyAdvice placeHoldersResolver(EnvironmentRepository environemntRepository) {
        return new PlaceholdersBodyAdvisor(environemntRepository);
    }

    @Bean
    @ConditionalOnBean(WritableResourceRepository.class)
    public WritableResourceController writableResourceController(WritableResourceRepository repository) {
        WritableResourceController controller = new WritableResourceController(repository);
        return controller;
    }

}
