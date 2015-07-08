package com.rsa.asoc.sa.ui.investigation;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * The Spring Boot entry point.
 *
 * @author athielke
 * @since 11.0.0.0
 */
@SpringBootApplication
public class Application {

    /**
     * The application main entry point.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(Application.class)
                .showBanner(false)
                .run(args);
    }
}
