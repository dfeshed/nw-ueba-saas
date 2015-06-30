package com.rsa.asoc.sa.ui.investigation.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Customize Spring MVCs static resource mappings to include the resources packaged from the client jar.
 *
 * @author athielke
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
}
