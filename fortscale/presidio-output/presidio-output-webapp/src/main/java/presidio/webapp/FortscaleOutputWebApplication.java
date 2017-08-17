package presidio.webapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.webapp.spring.OutputWebappConfiguration;

@SpringBootApplication
@ComponentScan(
		excludeFilters = {@ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"),
				@ComponentScan.Filter( type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleOutputWebApplication extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(FortscaleOutputWebApplication.class, OutputWebappConfiguration.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(new Object[]{FortscaleOutputWebApplication.class, OutputWebappConfiguration.class}, args);
	}
}
