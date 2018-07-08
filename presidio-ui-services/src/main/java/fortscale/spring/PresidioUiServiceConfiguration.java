package fortscale.spring;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.domain.rest.UserRestFilter;
import fortscale.domain.spring.PresidioUiDomainConfiguration;
import fortscale.presidio.output.client.api.AlertsPresidioOutputClient;
import fortscale.presidio.output.client.api.UsersPresidioOutputClient;
import fortscale.services.*;
import fortscale.services.cache.MemoryBasedCache;
import fortscale.services.impl.*;
import fortscale.services.presidio.core.converters.AggregationConverterHelper;
import fortscale.services.presidio.core.converters.AlertConverterHelper;
import fortscale.services.presidio.core.converters.IndicatorConverter;
import fortscale.services.presidio.core.converters.UserConverterHelper;

import fortscale.utils.configurations.ConfigrationServerClientUtils;

import fortscale.utils.configurations.ConfigrationServerClientUtilsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import presidio.utils.spring.PresidioUiUtilsConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration

@Import({PresidioUiUtilsConfiguration.class, PresidioUiDomainConfiguration.class,
        PresidioUiCommonConfig.class,
        PresidioUiOutputClientConfig.class})
public class PresidioUiServiceConfiguration {


    @Value("${presidio.themes.module.name}")
    String themesModule;

    @Value("${presidio.themes.default.profile.name}")
    String themesProfile;


    @Bean
    LocalizationService localizationService(){
        MemoryBasedCache memoryBasedCache = new MemoryBasedCache(1000,3600,String.class);
        return new LocalizationServiceImpl(memoryBasedCache, configrationServerClientUtils);
    }


    @Bean
    AlertsService alertsService(){

        AggregationConverterHelper aggregationConverterHelper= new AggregationConverterHelper();
        AlertConverterHelper alertConverterHelper = new AlertConverterHelper();
        return new AlertsServiceImpl(userService(),alertConverterHelper,aggregationConverterHelper, remoteAlertClientService);
    }


    @Bean
    UserService userService(){
        UserConverterHelper userConverterHelper = new UserConverterHelper();
        AggregationConverterHelper aggregationConverterHelper = new AggregationConverterHelper();
        return new UserServiceImpl(userConverterHelper, aggregationConverterHelper,remoteUsersClientService) ;

    }

    @Bean
    EvidencesService evidencesService() throws Exception {
        Properties properties = configrationServerClientUtils.readConfigurationAsProperties("application-presidio",null);

        String adminServerHostName = properties.getProperty("uiIntegration.adminServer");
        String brokerEndPointId = properties.getProperty("uiIntegration.brokerId");
        NwInvestigateHelper investigateHelper = new NwInvestigateHelperImpl(adminServerHostName, brokerEndPointId);
        IndicatorConverter indicatorConverter = new IndicatorConverter();
        return new EvidencesServiceImpl(dataEntitiesConfig,userService(),indicatorConverter,remoteAlertClientService,investigateHelper) ;

    }

    @Bean
    ThemesServiceImpl themesService(){
        return new ThemesServiceImpl(themesModule,themesProfile,configrationServerClientUtils);
    }


    @Bean
    ApplicationConfigurationService applicationConfigurationService(){
        return new ApplicationConfigurationServiceImpl(applicationConfigurationRepository,localizationService());
    }


    @Bean
    ShaPasswordEncoder shaPasswordEncoder(){
        ShaPasswordEncoder sha = new ShaPasswordEncoder(256);
        sha.setIterations(1000);
        return sha;

    }

    @Bean
    UserServiceFacade userServiceFacade(){
        return new UserServiceFacadeImpl(userService());
    }

    @Bean
    UserTagService userTagService(){
        return new UserTagsServiceImpl();
    }

    @Value("${users.with.alerts.service.cache.max.items:10}")
    int usersWithAlertsServiceCacheMaxSize;

    @Value("${users.with.alerts.service.cache.timeToExpireSec:600}")
    int usersWithAlertsServiceCacheTtl;
    @Bean
    UserWithAlertService userWithAlertService(){
        MemoryBasedCache<UserRestFilter, List<User>> memoryBasedCache =
                new MemoryBasedCache(usersWithAlertsServiceCacheMaxSize,usersWithAlertsServiceCacheTtl,ArrayList.class);
        return new UserWithAlertServiceImpl(userService(),alertsService(),memoryBasedCache);
    }

    @Autowired
    private ApplicationConfigurationRepository applicationConfigurationRepository;

    @Autowired
    private DataEntitiesConfig dataEntitiesConfig;


    @Autowired
    private ConfigrationServerClientUtils configrationServerClientUtils;

    @Autowired
    private AlertsPresidioOutputClient remoteAlertClientService;

    @Autowired
    private UsersPresidioOutputClient remoteUsersClientService;
}
