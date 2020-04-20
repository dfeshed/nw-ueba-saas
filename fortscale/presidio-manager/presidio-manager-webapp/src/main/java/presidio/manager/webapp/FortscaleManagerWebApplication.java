package presidio.manager.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")
})
@EnableAutoConfiguration(exclude = {ElasticsearchDataAutoConfiguration.class, ElasticsearchAutoConfiguration.class})
@SuppressWarnings("SpringFacetCodeInspection")
public class FortscaleManagerWebApplication {
    public static void main(String[] args) {
        Class[] sources = {FortscaleManagerWebApplication.class, ManagerWebappConfiguration.class};
        SpringApplication.run(sources, args);
    }
}
