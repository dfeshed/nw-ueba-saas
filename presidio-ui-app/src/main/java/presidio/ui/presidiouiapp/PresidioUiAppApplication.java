package presidio.ui.presidiouiapp;

import fortscale.spring.PresidioUiServiceConfiguration;
import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import presidio.ui.presidiouiapp.spring.WebConf;

//@ImportResource({"META-INF/spring/spring-old.xml"})
@Import({SpringMongoConfiguration.class,
         WebConf.class})
@SpringBootApplication
@ComponentScan(
        excludeFilters = {@ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter( type = FilterType.REGEX, pattern = "presidio.*")})
public class PresidioUiAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(PresidioUiAppApplication.class, args);
    }
}
