package presidio.input.pre.processing.application;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class PresidioInputPreProcessingApplication {
    public static void main(String[] args) {
        List<Class> configurationClasses = new ArrayList<>();
        configurationClasses.add(PresidioInputPreProcessingConfiguration.class);
        new PresidioShellableApplication().run(configurationClasses, args);
    }
}
