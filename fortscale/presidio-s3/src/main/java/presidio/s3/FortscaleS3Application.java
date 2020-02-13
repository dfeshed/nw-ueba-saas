package presidio.s3;

import fortscale.common.shell.PresidioShellableApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.s3.config.S3ServiceConfig;

import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@ComponentScan(excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = {"fortscale.*", "presidio.*"}))
public class FortscaleS3Application {

    public static void main(String[] args) {
        List<Class> configurationClasses = new ArrayList<>();
        // The Spring configuration of the application
        configurationClasses.add(S3ServiceConfig.class);
        new PresidioShellableApplication().run(configurationClasses, args);
    }
}
