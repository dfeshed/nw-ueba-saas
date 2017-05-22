package fortscale;


import fortscale.spring.InputSdkImplConfig;
import fortscale.utils.mongodb.config.MongoConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


@SpringBootApplication
@ComponentScan(
		excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"))
@EnableTask
public class Application {


	private static  Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		log.info("shay");
		SpringApplication.run(new Object[]{Application.class, InputSdkImplConfig.class,MongoConfig.class}, args);
	}


}
