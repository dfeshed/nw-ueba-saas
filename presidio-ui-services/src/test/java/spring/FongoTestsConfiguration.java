package spring;

import com.github.fakemongo.Fongo;
import com.mongodb.Mongo;
import fortscale.domain.core.EmailAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

@Configuration
public class FongoTestsConfiguration extends AbstractMongoConfiguration {

    @Bean
    EmailAddress.EmailAddressToStringConverter emailAddressToStringConverter(){
        return new EmailAddress.EmailAddressToStringConverter();
    }

    @Bean
    EmailAddress.StringToEmailAddressConverter stringToEmailAddressConverter(){
        return new EmailAddress.StringToEmailAddressConverter();
    }

    @Override
    protected String getDatabaseName () {
        return "fongo-test-db";
    }

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        return new Fongo("Fongo").getMongo();
    }
}
