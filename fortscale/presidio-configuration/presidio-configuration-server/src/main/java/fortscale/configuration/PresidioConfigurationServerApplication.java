package fortscale.configuration;

import fortscale.configuration.spring.PresidioConfigServerConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;

@EnableConfigServer
@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@Import(PresidioConfigServerConfiguration.class)
public class PresidioConfigurationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresidioConfigurationServerApplication.class, args);
    }
}
