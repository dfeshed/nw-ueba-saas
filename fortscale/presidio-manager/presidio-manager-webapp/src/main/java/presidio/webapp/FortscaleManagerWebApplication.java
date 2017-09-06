package presidio.webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.webapp.spring.ManagerWebappConfiguration;

@SpringBootApplication
@ComponentScan(
		excludeFilters = {@ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"),
				@ComponentScan.Filter( type = FilterType.REGEX, pattern = "presidio.*")})
@EnableAutoConfiguration(exclude = {ElasticsearchDataAutoConfiguration.class, ElasticsearchAutoConfiguration.class})
public class FortscaleManagerWebApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(new Object[]{FortscaleManagerWebApplication.class, ManagerWebappConfiguration.class}, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(FortscaleManagerWebApplication.class,ManagerWebappConfiguration.class);
	}
}

