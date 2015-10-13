package com.rsa.asoc.sa.ui.common.config;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Base configuration and customization for Spring MVC.
 *
 * @author athielke
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        return new Jackson2ObjectMapperBuilder()
                // Force Jackson to serialize Instants as integers instead of floats
                .featuresToDisable(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS);
    }
}
