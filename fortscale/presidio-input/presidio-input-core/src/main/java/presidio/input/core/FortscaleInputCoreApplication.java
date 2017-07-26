package presidio.input.core;




import fortscale.common.general.PresidioShellableApplication;
import fortscale.common.shell.config.ShellCommonCommandsConfig;
import fortscale.utils.logging.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.input.core.spring.InputProductionConfiguration;

@SpringBootApplication
@ComponentScan(
        excludeFilters = { //only scan for spring-boot beans
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleInputCoreApplication {


    public static void main(String[] args) {
        PresidioShellableApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputProductionConfiguration.class}, args);
    }

}


