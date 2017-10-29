package presidio.input.core;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.PresidioCommands;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputProductionConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleInputCoreApplication {

    public static void main(String[] args) {
        List<Class> sources = new ArrayList<>();
        sources.add(FortscaleInputCoreApplication.class);
        sources.add(InputProductionConfiguration.class);

        // The supported CLI commands for the application
        sources.add(PresidioCommands.class);

        new PresidioShellableApplication().run(sources, args);
    }

}


