package presidio;


import presidio.spring.InputProcessConfiguration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(
		excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
						  @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")
						})
@EnableTask
public class FortscaleInputCoreApplication{


	private static  Logger log = LoggerFactory.getLogger(FortscaleInputCoreApplication.class);

	public static void main(String[] args) {
		log.info("shay");
		SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputProcessConfiguration.class}, args);
	}


}
