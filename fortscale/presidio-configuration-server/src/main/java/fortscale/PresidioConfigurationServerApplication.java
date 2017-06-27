package fortscale;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@EnableConfigServer
@SpringBootApplication
public class PresidioConfigurationServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresidioConfigurationServerApplication.class, args);
    }
}
