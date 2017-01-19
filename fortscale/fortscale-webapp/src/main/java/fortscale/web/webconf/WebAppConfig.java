package fortscale.web.webconf;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.services.ActiveDirectoryService;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;
import fortscale.web.beans.DataBean;
import fortscale.web.exceptions.handlers.FortscaleRestErrorResolver;
import fortscale.web.exceptions.handlers.RestExceptionHandler;
import fortscale.web.extensions.FortscaleCustomEditorService;
import fortscale.web.extensions.RenamingProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shays on 01/01/2017.
 * Load webapp spring context
 */


@Configuration
@EnableSpringConfigured
@EnableAnnotationConfiguration
@EnableWebMvc
@EnableSwagger2
//Scan and init all controllers
@ComponentScan(basePackages = "fortscale.web", useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
)
@ImportResource({"classpath*:META-INF/spring/fortscale-logging-context.xml"})
@Import({GlobalConfiguration.class, AdTaskConfig.class,SwaggerConfig.class})
//Load properties files:
@PropertySource({"classpath:META-INF/application-config.properties","classpath:META-INF/entities-overriding.properties","classpath:META-INF/evidence.events.filtering.properties"})
public class WebAppConfig extends WebMvcConfigurerAdapter {


    public static final int DEFAULT_CACHE_PERIOD_SECONDS = 3600 * 24; //Default time to keep resource in seconds
    public static final String CACHE_PERIOD_KEY= "webapp.configurations.cache_time_period";

    private static Logger logger = Logger.getLogger(WebAppConfig.class);

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    private ActiveDirectoryService activeDirectoryService;



    /**
     * tells the browser to save the resource for X seconds by define cache-ontrol header with max-age
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        int cachePeriodConf  = initCachePeriodValue();
        logger.info("Control-Cache/Max-Age for static resource set to {}",cachePeriodConf);

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

        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
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
    FortscaleCustomEditorService fortscaleCustomEditorService(){
        return new FortscaleCustomEditorService();
    }

    @Bean
    public RenamingProcessor renamingProcessor(){
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
     * @return
     */
    //@Bean(name = "exceptionHandlerExceptionResolver")
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver(){

        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setOrder(0);
        return  exceptionHandlerExceptionResolver;
    }

    /**
     * Init fortscale error resolver for rest not found exceptions
     * @return
     */
    //@Bean(name="restExceptionResolver")
    RestExceptionHandler restExceptionResolver(){
        LocaleResolver localeResolver =  new org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver();

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

}
