package fortscale;

import org.slf4j.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;


@SpringBootApplication()
@EnableTask
public class FortscaleInputCoreApplication{// implements CommandLineRunner {



	public static void main(String[] args) {
		SpringApplication.run(FortscaleInputCoreApplication.class, args);
	}



	//@Override //This method called by CommandLineRunner, and override ti
//	public void run(String... args) throws Exception {
		//logger.info("shay");
//
//
//	}
}
