package presidio.webapp;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import presidio.webapp.spring.OutputWebappConfiguration;
import presidio.webapp.spring.OutputWebappProductionConfiguration;

/**
 * Output webapp application expose the output REST APIs for retrieving presidio alerts, users, supporting info.
 * -When running this application as a war deployed on tomcat the REST request path should be-
 * http://<machine>:8080/presidio-output/alerts
 * -When running the application as standalone war (e.g. java -jar) the request path is-
 * http://<machine>:1234/alerts
 */
@SpringBootApplication
@ComponentScan(
        excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "fortscale.*"),
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "presidio.*")})
public class FortscaleOutputWebApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(new Object[]{FortscaleOutputWebApplication.class,
                OutputWebappProductionConfiguration.class});
    }

    public static void main(String[] args) {
        SpringApplication.run(new Object[]{FortscaleOutputWebApplication.class, OutputWebappProductionConfiguration.class}, args);
    }
}
