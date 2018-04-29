package fortscale.spring;

import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.ApplicationConfigurationRepository;
import fortscale.domain.rest.UserRestFilter;
import fortscale.domain.spring.PresidioUiDomainConfiguration;
import fortscale.services.*;
import fortscale.services.cache.MemoryBasedCache;
import fortscale.services.impl.*;
import fortscale.services.presidio.core.converters.AggregationConverterHelper;
import fortscale.services.presidio.core.converters.AlertConverterHelper;
import fortscale.services.presidio.core.converters.IndicatorConverter;
import fortscale.services.presidio.core.converters.UserConverterHelper;
import fortscale.utils.configurations.ConfigrationServerClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.web.client.RestTemplate;
import presidio.utils.spring.PresidioUiUtilsConfiguration;

import java.util.ArrayList;
import java.util.List;

@Configuration

@Import({PresidioUiUtilsConfiguration.class, PresidioUiDomainConfiguration.class,
        PresidioUiCommonConfig.class})
public class PresidioUiServiceConfiguration {



    @Value("${spring.cloud.config.uri}")
    String configurationServerUrl;

    @Value("${spring.cloud.config.username}")
    String configurationServerUserName;

    @Value("${spring.cloud.config.password}")
    String configurationServerUserNamePassword;

    @Value("${presidio.themes.module.name}")
    String themesModule;

    @Value("${presidio.themes.default.profile.name}")
    String themesProfile;




    @Bean
    AlertCommentsService alertCommentsService(){
        return new AlertCommentsServiceImpl();
    }

    @Bean
    ConfigrationServerClientUtils configrationServerClientUtils(){
        RestTemplate restTemplate = new RestTemplate();
        return new ConfigrationServerClientUtils(restTemplate,configurationServerUrl,configurationServerUserName,configurationServerUserNamePassword);
    }
    @Bean
    LocalizationService localizationService(){
        MemoryBasedCache memoryBasedCache = new MemoryBasedCache(1000,3600,String.class);
        return new LocalizationServiceImpl(memoryBasedCache, applicationConfigurationService(),configrationServerClientUtils());
    }

    @Bean
    AlertsService alertsService(){
        AggregationConverterHelper aggregationConverterHelper= new AggregationConverterHelper();
        AlertConverterHelper alertConverterHelper = new AlertConverterHelper();
        return new AlertsServiceImpl(userService(),alertConverterHelper,alertCommentsService(),aggregationConverterHelper);
    }


    @Bean
    UserService userService(){
        UserConverterHelper userConverterHelper = new UserConverterHelper();
        AggregationConverterHelper aggregationConverterHelper = new AggregationConverterHelper();
        return new UserServiceImpl(userConverterHelper, aggregationConverterHelper) ;

    }

    @Bean
    EvidencesService evidencesService(){

        IndicatorConverter indicatorConverter = new IndicatorConverter();
        return new EvidencesServiceImpl(dataEntitiesConfig,userService(),indicatorConverter) ;

    }

    @Bean
    ThemesServiceImpl themesService(){
        return new ThemesServiceImpl(themesModule,themesProfile,configrationServerClientUtils());
    }


    @Bean
    ApplicationConfigurationService applicationConfigurationService(){
        return new ApplicationConfigurationServiceImpl(applicationConfigurationRepository);
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


}
