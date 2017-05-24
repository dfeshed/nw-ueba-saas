package presidio.sdk.impl;


import fortscale.utils.mongodb.config.MongoConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.sdk.impl.spring.CoreManagerSdkConfig;

@SpringBootApplication
@ComponentScan(
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"))
public class Application {


    private static Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        log.info("core manager is running...");
        SpringApplication.run(new Object[]{Application.class, MongoConfig.class, CoreManagerSdkConfig.class}, args);
    }


}
