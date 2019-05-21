package presidio.ui.presidiouiapp.spring;


import presidio.rsa.auth.PresidioNwAuthenticationConfig;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.services.*;


import fortscale.spring.PresidioUiServiceConfiguration;

import fortscale.utils.FilteringPropertiesConfigurationHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import presidio.rsa.auth.mock.PresidioNwAuthenticationMockConfig;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import presidio.ui.presidiouiapp.beans.request.AlertFilterHelperImpl;
import presidio.ui.presidiouiapp.extensions.FortscaleCustomEditorService;
import presidio.ui.presidiouiapp.extensions.RenamingProcessor;
import presidio.ui.presidiouiapp.rest.*;
import presidio.ui.presidiouiapp.rest.errorhandler.RestErrorHandler;

import java.util.List;


/**
 * Created by shays on 01/01/2017.
 * Load webapp spring context
 */


@Configuration
@Import({PresidioUiServiceConfiguration.class, PresidioNwAuthenticationConfig.class, PresidioNwAuthenticationMockConfig.class})
//TODO: when moving to spring 5, WebMvcConfigurerAdapter, and instead we need to implement WebMvcConfigurer
public class WebConf extends WebMvcConfigurerAdapter{


    @Bean
    BindindControllerAdvice bindindControllerAdvice(){
        return new BindindControllerAdvice();
    }

    @Bean
    FortscaleCustomEditorService fortscaleCustomEditorService(){
        return new FortscaleCustomEditorService();
    }
    @Bean
    ApiController apiController(){
        return new ApiController(dataEntitiesConfig, entityServiceFacade);
    }


    @Bean
    ApiAlertController alertController(){

        AlertFilterHelperImpl alertFilterHelper = new AlertFilterHelperImpl();
        return new ApiAlertController(alertFilterHelper,localizationService,alertsService);
    }

    @Bean
    ApiLocalization apiLocalization(){
        return new ApiLocalization(localizationService);
    }

    @Bean
    ApiApplicationConfigurationController apiApplicationConfigurationController(){
        return new ApiApplicationConfigurationController(applicationConfigurationService);
    }

    @Bean
    ApiEvidenceController apiEvidenceController(){
        FilteringPropertiesConfigurationHandler propertiesConfigurationHandler = new FilteringPropertiesConfigurationHandler();
        return new ApiEvidenceController(evidencesService,localizationService);
    }

    @Bean
    ApiThemes apiThemes(){
        return new ApiThemes(themesService);
    }

    @Bean
    ApiUserController apiUserController(){

        return new ApiUserController(entityServiceFacade, entityTagService, entityService, entityWithAlertService);
    }

    @Bean
    ApiAnalystController apiAnalystController(){
        return new ApiAnalystController();
    }

    @Bean
    ApiSystemSetupTagsController apiSystemSetupTagsController(){
        return new ApiSystemSetupTagsController();
    }

    @Bean
    RestErrorHandler restErrorHandler(){
        return new RestErrorHandler();
    }

    @Bean
    RenamingProcessor renamingProcessor(){
        return new RenamingProcessor(true);
    }

    /**
     * Adding naming processor as argument resolver
     * The naming processor init and register FortscaleCustomEditorService
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(renamingProcessor());

    }



    @Autowired
    EntityServiceFacade entityServiceFacade;

    @Autowired
    EntityTagService entityTagService;

    @Autowired
    EntityWithAlertService entityWithAlertService;

    @Autowired
    EntityService entityService;


    @Autowired
    ThemesService themesService;



    @Autowired
    LocalizationService localizationService;

    @Autowired
    AlertsService alertsService;

    @Autowired
    ApplicationConfigurationService applicationConfigurationService;

    @Autowired
    EvidencesService evidencesService;

    @Autowired
    DataEntitiesConfig dataEntitiesConfig;


}
