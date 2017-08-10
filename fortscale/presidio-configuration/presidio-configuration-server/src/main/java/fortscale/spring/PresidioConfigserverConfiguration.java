package fortscale.spring;

import fortscale.resource.*;
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
public class PresidioConfigserverConfiguration {

    @Autowired
    private ResourceLoader resourceLoader;

//    @Configuration
//    @ConditionalOnMissingBean(ResourceWritableRepository.class)
//    protected static class DefaultRepositoryConfiguration {
//
//        @Bean
//        @ConditionalOnBean(value = {SearchPathLocator.class,JGitEnvironmentRepository.class})
//        public JGitResourceWritableRepository resourceWritableRepository(SearchPathLocator service, JGitEnvironmentRepository gitAccessor) {
//            JGitResourceWritableRepository repository = new JGitResourceWritableRepository(service, gitAccessor);
//            return repository;
//        }
//    }
//
//    @Configuration
//    @Profile("native")
//    protected static class NativeResourceWritableRepository {
//
//        @Bean
//        @ConditionalOnBean(SearchPathLocator.class)
//        public ResourceWritableRepository resourceWritableRepository(SearchPathLocator service) {
//            return new GenericResourceWritableRepository(service);
//        }
//    }

//    @Bean
//    @Profile("native")
//    public ResourceWritableRepository resourceWritableRepository(SearchPathLocator service) {
//        return new GenericResourceWritableRepository(service);
//    }


   // @ConditionalOnMissingBean(ResourceWritableRepository.class)
    @Bean
    public ResourceWritableRepository resourceWritableRepository(SearchPathLocator service, JGitEnvironmentRepository gitAccessor) {
        return new JGitResourceWritableRepository(service, gitAccessor);
    }

    @Bean
    public ResponseBodyAdvice responseBodyAdvice(EnvironmentRepository environemntRepository) {
        return new PlaceholdersBodyAdvisor(environemntRepository);
    }

    @Bean
    @ConditionalOnBean(ResourceWritableRepository.class)
    public ResourceWriteController resourceWriteController(ResourceWritableRepository repository) {
        ResourceWriteController controller = new ResourceWriteController(repository);
        return controller;
    }

}
