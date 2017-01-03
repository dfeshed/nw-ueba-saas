package fortscale.web.webconf;

import fortscale.global.configuration.GlobalConfiguration;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.logging.Logger;


import fortscale.web.exceptions.handlers.FortscaleRestErrorResolver;
import fortscale.web.exceptions.handlers.RestExceptionHandler;
import fortscale.web.extensions.FortscaleCustomEditorService;
import fortscale.web.extensions.RenamingProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;


import org.springframework.data.hadoop.config.common.annotation.EnableAnnotationConfiguration;
import org.springframework.http.MediaType;

import org.springframework.stereotype.Controller;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * Created by shays on 01/01/2017.
 *
 * Java Configuration that replacing parts of webapp-config.xml
 * Todo: should move all configurations webapp-config.xml and remove the file when done (opened Jira https://fortscale.atlassian.net/browse/FV-13388)
 */


@Configuration
@EnableSpringConfigured
@EnableAnnotationConfiguration
@EnableWebMvc
@ComponentScan(basePackages = "fortscale", useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Controller.class)
)
@ImportResource({"classpath*:META-INF/spring/fortscale-logging-context.xml"})
@Import(GlobalConfiguration.class)
@PropertySource({"classpath:META-INF/application-config.properties","classpath:META-INF/entities-overriding.properties","classpath:META-INF/evidence.events.filtering.properties"})
public class WebAppConfig extends WebMvcConfigurerAdapter {





    public static final int DEFAULT_CACHE_PERIOD_SECONDS = 3600 * 24; //Default time to keep resource in seconds
    public static final String CACHE_PERIOD_KEY= "webapp.configurations.cache_time_period";

    private static Logger logger = Logger.getLogger(WebAppConfig.class);

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    @Autowired
    private FortscaleCustomEditorService fortscaleCustomEditorService;

    /**
     *
     */
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
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(renamingProcessor());
    }

    @Bean
    public RenamingProcessor renamingProcessor(){
        return new RenamingProcessor(requestMappingHandlerAdapter,fortscaleCustomEditorService, true);
    }

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//        configurer.enable();
//    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new MyCustomInterceptor())
//                .addPathPatterns("/**")
//                .excludePathPatterns("/foo/**");
//    }


    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.ignoreUnknownPathExtensions(false).defaultContentType(MediaType.TEXT_HTML);


    }
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





    @Bean(name = "exceptionHandlerExceptionResolver")
    public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver(){
        ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
        exceptionHandlerExceptionResolver.setOrder(0);
        return  exceptionHandlerExceptionResolver;
    }

    @Bean(name="restExceptionResolver")
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

    public ApplicationConfigurationService getApplicationConfigurationService() {
        return applicationConfigurationService;
    }

    public void setApplicationConfigurationService(ApplicationConfigurationService applicationConfigurationService) {
        this.applicationConfigurationService = applicationConfigurationService;
    }

    public RequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
        return requestMappingHandlerAdapter;
    }

    public void setRequestMappingHandlerAdapter(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    public FortscaleCustomEditorService getFortscaleCustomEditorService() {
        return fortscaleCustomEditorService;
    }

    public void setFortscaleCustomEditorService(FortscaleCustomEditorService fortscaleCustomEditorService) {
        this.fortscaleCustomEditorService = fortscaleCustomEditorService;
    }
}
