package presidio.adapter;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.adapter.spring.AdapterConfig;

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
        List<Class> sources = Stream.of(AdapterConfig.class).collect(Collectors.toList());
        PresidioShellableApplication.run(sources, args);
    }
}
