package fortscale.web.webconf;

import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.ConfigrationServerClientUtils;
import fortscale.web.exceptions.handlers.FortscaleRestErrorResolver;
import fortscale.web.exceptions.handlers.RestExceptionHandler;
import fortscale.web.extensions.FortscaleCustomEditorService;
import fortscale.web.extensions.RenamingProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import presidio.utils.spring.PropertySourceConfigurer;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by shays on 01/01/2017.
 * Load webapp spring context
 */


@Configuration
@EnableSpringConfigured
@EnableWebMvc

@ComponentScan(basePackages = "fortscale")

@ImportResource({"classpath*:META-INF/spring/fortscale-logging-context.xml"})
@Import({ SwaggerConfig.class, MockDemoConfig.class})

//Load properties files:
@PropertySource({"classpath:META-INF/entities.properties",
        "classpath:META-INF/application-config.properties",
        "classpath:META-INF/entities-overriding.properties",
        "classpath:META-INF/evidence.events.filtering.properties"})
public class WebAppConfig extends WebMvcConfigurerAdapter {

    static Map<String, String> defaultProperties = new HashMap<>();

    static {
        defaultProperties.put("mongo.host.name", "localhost");
        defaultProperties.put("mongo.host.port", "27017");
        defaultProperties.put("mongo.db.name", "quest-ca");
        defaultProperties.put("mongo.db.user", "");
        defaultProperties.put("mongo.db.password", "");
        defaultProperties.put("mongo.map.dot.replacement", "#dot#");
        defaultProperties.put("mongo.map.dollar.replacement", "#dlr#");
        defaultProperties.put("presidio.output.webapp.url", "http://localhost:8080/presidio-output");
        defaultProperties.put("alert.affect.duration.days", "90");
    }

    public static final int DEFAULT_CACHE_PERIOD_SECONDS = 3600 * 24; //Default time to keep resource in seconds
    public static final String CACHE_PERIOD_KEY = "webapp.configurations.cache_time_period";

    private static Logger logger = Logger.getLogger(WebAppConfig.class);


    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;


    /**
     * tells the browser to save the resource for X seconds by define cache-ontrol header with max-age
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        int cachePeriodConf = initCachePeriodValue();
        logger.info("Control-Cache/Max-Age for static resource set to {}", cachePeriodConf);

        registry
                //.addResourceHandler("/*.html")
                .addResourceHandler("/**")
                .addResourceLocations("/resources/")
                .setCachePeriod(cachePeriodConf);
        // .resourceChain(true);
        //.addResolver(new PathResourceResolver());

        //All CSS & JS
        registry
                .addResourceHandler("/assets/**")
                .addResourceLocations("/resources/assets/")
                .setCachePeriod(cachePeriodConf)
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
     * Adding naming processor as argument resolver
     * The naming processor init and register FortscaleCustomEditorService
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(renamingProcessor());

    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    /**
     * @return
     */
    @Bean
    private static PropertySourceConfigurer loadPropertyConfigurerFromCentralServer() {
        Properties properties = null;
        try {
            ConfigrationServerClientUtils clientUtils = new ConfigrationServerClientUtils();
            properties = new ConfigrationServerClientUtils().readConfigurationAsProperties();
            properties = setDefaultValues(properties);
        } catch (Exception e) {
            //Fallback - for tests
            logger.error("Cannot read configuration from server, use fallback:");
            properties = setDefaultValues(properties);
        }

        return new PropertySourceConfigurer(WebAppConfig.class, properties);
    }

    private static Properties setDefaultValues(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }
        if (defaultProperties != null) {
            for (Map.Entry<String, String> property : defaultProperties.entrySet()) {
                properties.putIfAbsent(property.getKey(), property.getValue());
            }
        }
        return properties;

    }

    @Bean
    FortscaleCustomEditorService fortscaleCustomEditorService() {
        return new FortscaleCustomEditorService();
    }

    @Bean
    public RenamingProcessor renamingProcessor() {
        return new RenamingProcessor(true);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreUnknownPathExtensions(false).defaultContentType(MediaType.TEXT_HTML);
    }


    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        super.configureHandlerExceptionResolvers(exceptionResolvers);
        exceptionResolvers.add(exceptionHandlerExceptionResolver());
        exceptionResolvers.add(restExceptionResolver());
    }

    /**
     * Init spring execption resolver
     *
     * @return
     */
    //@Bean(name = "exceptionHandlerExceptionResolver")
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {

        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setOrder(0);
        return exceptionHandlerExceptionResolver;
    }

    /**
     * Init fortscale error resolver for rest not found exceptions
     *
     * @return
     */
    //@Bean(name="restExceptionResolver")
    RestExceptionHandler restExceptionResolver() {
        LocaleResolver localeResolver = new org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver();

        //Create exceptionMappingDefinitions
        Map exceptionMappingDefinitions = new HashMap();
        exceptionMappingDefinitions.put("fortscale.common.exceptions.UnknownResourceException", HttpServletResponse.SC_NOT_FOUND);


        //Create error resolver and set the locale resolver and exceptionMappingDefinitions
        FortscaleRestErrorResolver resolver = new FortscaleRestErrorResolver();
        resolver.setLocaleResolver(localeResolver);
        resolver.setExceptionMappingDefinitions(exceptionMappingDefinitions);

        //Create rest exception handler
        RestExceptionHandler handler = new RestExceptionHandler();
        handler.setOrder(100);
        handler.setErrorResolver(resolver);
        return handler;
    }


    //Use JSON view resolver as the only viewer
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        MappingJackson2JsonView jsonView = new MappingJackson2JsonView();
        jsonView.setPrettyPrint(true);


        registry.enableContentNegotiation(jsonView);
    }

    /**
     * Check if the cache period defined on mongo, is so use it, if not set the devault value to mongo and return it
     *
     * @return
     */
    private int initCachePeriodValue() {
        Integer cachePeriodConf = applicationConfigurationService.getApplicationConfigurationAsObject(CACHE_PERIOD_KEY, Integer.class);
        if (cachePeriodConf == null) {

            applicationConfigurationService.insertConfigItemAsObject(CACHE_PERIOD_KEY, DEFAULT_CACHE_PERIOD_SECONDS);
            return DEFAULT_CACHE_PERIOD_SECONDS;
        } else {

            return cachePeriodConf;
        }
    }

}
