package presidio.adapter;

import fortscale.common.shell.PresidioShellableApplication;
import fortscale.common.shell.command.PresidioCommands;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.adapter.spring.AdapterConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@SpringBootApplication
@ComponentScan( //only scan for spring-boot beans
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleAdapterApplication {

    public static void main(String[] args) {
        List<Class> sources = new ArrayList<>();
        sources.add(AdapterConfig.class);

        // The supported CLI commands for the application
        sources.add(PresidioCommands.class);

        new PresidioShellableApplication().run(sources, args);
    }
}
