package fortscale.web.webconf;

import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.rest.ApiActiveDirectoryController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;

/**
 * Created by shays on 01/01/2017.
 *
 * Java Configuration that replacing parts of webapp-config.xml
 * Todo: should move all configurations webapp-config.xml and remove the file when done (opened Jira https://fortscale.atlassian.net/browse/FV-13388)
 */
@Configuration
@EnableWebMvc
public class WebAppConfig extends WebMvcConfigurerAdapter {




    public static final int DEFAULT_CACHE_PERIOD_SECONDS = 3600 * 24; //Default time to keep resource in seconds
    public static final String CACHE_PERIOD_KEY= "webapp.configurations.cache_time_period";

    private static Logger logger = Logger.getLogger(WebAppConfig.class);

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    /**
     * tells the browser to save the resource for X seconds by define cache-ontrol header with max-age
     *
     * @param registry
     */
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        int cachePeriodConf  = initCachePeriodValue();
        logger.info("Control-Cache/Max-Age for static resource set to {}",cachePeriodConf);

        registry
                .addResourceHandler("/**")
                .addResourceLocations("/resources/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //All CSS & JS
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //All old JSON files
        registry
                .addResourceHandler("/app/**")
                .addResourceLocations("/resources/app/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //All images
        registry
                .addResourceHandler("/images/**")
                .addResourceLocations("/resources/images/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //All libs js
        registry
                .addResourceHandler("/libs/**")
                .addResourceLocations("/resources/libs/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        // All all widgets
        registry
                .addResourceHandler("/widgets/**")
                .addResourceLocations("/resources/widgets/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());

        //All old JSON files
        registry
                .addResourceHandler("/data/**")
                .addResourceLocations("/resources/data/")
                .setCachePeriod(cachePeriodConf)
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
    }

    /**
     * Check if the cache period defined on mongo, is so use it, if not set the devault value to mongo and return it
     * @return
     */
    private int initCachePeriodValue() {
        Integer cachePeriodConf = applicationConfigurationService.getApplicationConfigurationAsObject(CACHE_PERIOD_KEY, Integer.class);
        if (cachePeriodConf == null){

            applicationConfigurationService.insertConfigItemAsObject(CACHE_PERIOD_KEY, DEFAULT_CACHE_PERIOD_SECONDS);
            return DEFAULT_CACHE_PERIOD_SECONDS;
        } else {

            return cachePeriodConf;
        }
    }

    public ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }
}
