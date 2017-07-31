package presidio.webapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.webapp.spring.OutputWebappConfiguration;

@SpringBootApplication
@ComponentScan(
		excludeFilters = {@ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"),
				@ComponentScan.Filter( type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleOutputWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(new Object[]{FortscaleOutputWebApplication.class, OutputWebappConfiguration.class}, args);
	}
}
