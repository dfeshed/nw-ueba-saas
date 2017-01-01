package fortscale.web.webconf;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Created by shays on 01/01/2017.
 */
@Configuration
@EnableWebMvc
public class WebAppConfig extends WebMvcConfigurerAdapter {

    public static final int CACHE_PERIOD = 3600 * 24;

    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/**")
                .addResourceLocations("/resources/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/")
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry
                .addResourceHandler("/app/**")
                .addResourceLocations("/resources/app/")
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("/resources/images/")
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry
                .addResourceHandler("/libs/**")
                .addResourceLocations("/resources/libs/")
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        registry
                .addResourceHandler("/widgets/**")
                .addResourceLocations("/resources/widgets/")
                .setCachePeriod(CACHE_PERIOD)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }
}
