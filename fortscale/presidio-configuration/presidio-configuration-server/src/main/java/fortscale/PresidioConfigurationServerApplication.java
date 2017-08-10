package fortscale;

import fortscale.spring.PresidioConfigserverConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.context.annotation.Import;

@EnableConfigServer
@SpringBootApplication
@Import(PresidioConfigserverConfiguration.class)
public class PresidioConfigurationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresidioConfigurationServerApplication.class, args);
    }
}
