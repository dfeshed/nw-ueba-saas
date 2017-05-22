package fortscale;

import fortscale.spring.WebConf;
//import fortscale.utils.mongodb.config.MongoConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
		excludeFilters = @ComponentScan.Filter( type = FilterType.REGEX, pattern = "fortscale.*"))
public class FortscaleOutputApplication {

	public static void main(String[] args) {
		SpringApplication.run(new Object[]{FortscaleOutputApplication.class, WebConf.class}, args);
	}
}
