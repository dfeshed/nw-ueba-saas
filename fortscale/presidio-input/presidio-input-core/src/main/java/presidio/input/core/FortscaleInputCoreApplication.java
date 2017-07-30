package presidio.input.core;




import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.config.ShellableApplicationConfig;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputProductionConfiguration;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
@EnableAutoConfiguration(exclude={ElasticsearchAutoConfiguration.class, ElasticsearchDataAutoConfiguration.class})
public class FortscaleInputCoreApplication {

    public static void main(String[] args) {
        List<Class> sources = Stream.of(FortscaleInputCoreApplication.class, InputProductionConfiguration.class).collect(Collectors.toList());
        PresidioShellableApplication.run(sources, args);
    }

}


