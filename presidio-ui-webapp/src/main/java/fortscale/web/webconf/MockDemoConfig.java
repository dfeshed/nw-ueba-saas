package fortscale.web.webconf;

import fortscale.utils.logging.LoggerFilter;
import fortscale.utils.servlet.RootFilter;
import fortscale.web.demoservices.DemoBuilder;
import fortscale.web.rest.ApiApplicationConfigurationController;
import fortscale.web.rest.BindindControllerAdvice;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by shays on 04/07/2017.
 */
@Configuration
public class MockDemoConfig {

//
//    @Bean(name = "loggerFilter")
//    public LoggerFilter loggerFilter(){
//        return new LoggerFilter();
//    }

    @Bean(name = "demoBuilder")
    public DemoBuilder demoBuilder(){
        return new DemoBuilder();
    }
//
//    @Bean(name= "rootFilter")
//    public RootFilter rootFilter(){
//        return new RootFilter();
//    }
//
////    @Bean
////    BindindControllerAdvice bindindControllerAdvice(){
////        return new BindindControllerAdvice();
////    }
////
////    @Bean
////    ApiApplicationConfigurationController apiApplicationConfigurationController(){
////        return new ApiApplicationConfigurationController();
////    }
}
