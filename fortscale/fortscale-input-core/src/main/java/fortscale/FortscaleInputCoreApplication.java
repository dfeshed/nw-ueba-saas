package fortscale;

import fortscale.spring.InputProcessConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;



@SpringBootApplication()
@EnableTask
public class FortscaleInputCoreApplication{// implements CommandLineRunner {



	public static void main(String[] args) {
		SpringApplication.run(new Object[]{FortscaleInputCoreApplication.class, InputProcessConfiguration.class}, args);
	}


}
