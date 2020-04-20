package fortscale.domain.spring;

import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories({ "fortscale.domain" })
@EnableMongoAuditing
@Profile("!mock-mongo")
public class PresidioUiDomainConfiguration {

    @Bean("fsMongoConfiguration")
    public AbstractMongoConfiguration fsMongoConfiguration(){
        return new SpringMongoConfiguration();
    }
}
