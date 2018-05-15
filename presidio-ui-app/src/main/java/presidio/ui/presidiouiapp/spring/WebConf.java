package presidio.ui.presidiouiapp.spring;


import presidio.rsa.auth.PresidioNwAuthenticationConfig;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.services.*;


import fortscale.spring.PresidioUiServiceConfiguration;

import fortscale.utils.FilteringPropertiesConfigurationHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;

import presidio.ui.presidiouiapp.beans.request.AlertFilterHelperImpl;
import presidio.ui.presidiouiapp.extensions.FortscaleCustomEditorService;
import presidio.ui.presidiouiapp.rest.*;
import presidio.ui.presidiouiapp.rest.errorhandler.RestErrorHandler;


/**
 * Created by shays on 01/01/2017.
 * Load webapp spring context
 */


@Configuration
@Import({PresidioUiServiceConfiguration.class, PresidioNwAuthenticationConfig.class})
public class WebConf{


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
        return new ApiController(dataEntitiesConfig,userServiceFacade);
    }


    @Bean
    ApiAlertController alertController(){

        AlertFilterHelperImpl alertFilterHelper = new AlertFilterHelperImpl();
        return new ApiAlertController(alertCommentsService,alertFilterHelper,localizationService,alertsService);
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

        return new ApiUserController(userServiceFacade,userTagService,userService,userWithAlertService);
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

    @Autowired
    UserServiceFacade userServiceFacade;

    @Autowired
    UserTagService userTagService;

    @Autowired
    UserWithAlertService userWithAlertService;

    @Autowired
    UserService userService;


    @Autowired
    ThemesService themesService;


    @Autowired
    AlertCommentsService alertCommentsService;


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
