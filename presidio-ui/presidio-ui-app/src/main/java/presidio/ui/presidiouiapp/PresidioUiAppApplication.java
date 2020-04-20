package presidio.ui.presidiouiapp;

import fortscale.presidio.db.mongo.MongodbMockConfig;
import fortscale.presidio.remote.conf.spring.PresidioUiRemoteConfigurationClientMockConfiguration;
import fortscale.spring.PresidioUiRemoteConfigurationClientConfiguration;
import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

import presidio.ui.presidiouiapp.spring.WebConf;


@Import({SpringMongoConfiguration.class, MongodbMockConfig.class,
         WebConf.class,PresidioUiRemoteConfigurationClientConfiguration.class,PresidioUiRemoteConfigurationClientMockConfiguration.class})
@SpringBootApplication
@ComponentScan(
        excludeFilters = {@ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter( type = FilterType.REGEX, pattern = "presidio.*")})
public class PresidioUiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresidioUiAppApplication.class, args);
    }
}
