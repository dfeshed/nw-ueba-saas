package fortscale.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("fortscale.presidio.output.client.spring") //Scan on configuration class to avoid direct dependency
public class PresidioUiOutputClientConfig {



}
