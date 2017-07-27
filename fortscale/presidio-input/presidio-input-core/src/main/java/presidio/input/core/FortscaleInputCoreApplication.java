package presidio.input.core;




import fortscale.common.general.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputProductionConfiguration;

@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableAutoConfiguration(exclude={ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class FortscaleInputCoreApplication {


    public static void main(String[] args) {
        PresidioShellableApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputProductionConfiguration.class}, args);
    }

}


