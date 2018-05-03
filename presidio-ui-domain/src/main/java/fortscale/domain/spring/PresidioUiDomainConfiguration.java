package fortscale.domain.spring;

import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({ "fortscale.domain" })
@EnableMongoAuditing
public class PresidioUiDomainConfiguration {

    @Bean("fsMongoConfiguration")
    public SpringMongoConfiguration fsMongoConfiguration(){
        return new SpringMongoConfiguration();
    }
}
