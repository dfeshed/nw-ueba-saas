package com.rsa.asoc.sa.ui.common.config;

import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Base configuration and customization for Spring MVC.
 *
 * @author athielke
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    public static final String SESSION_COOKIE_NAME = "SASESSIONID";

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder()
                // Force Jackson to serialize Instants as integers instead of floats
                .featuresToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }

    @Bean
    public ServletContextInitializer servletContextInitializer() {
        // override the default session cookie name (jsessionid) so it does not clash with SA Classic
        return servletContext -> servletContext.getSessionCookieConfig().setName(SESSION_COOKIE_NAME);
    }
}
