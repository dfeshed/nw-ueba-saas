package presidio.input.core.spring;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.io.Resource;
import presidio.input.core.services.converters.ConverterService;
import presidio.input.core.services.converters.ConverterServiceImpl;
import presidio.input.core.services.converters.ade.*;
import presidio.input.core.services.converters.output.*;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputCoreManager;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.TransformationService;
import presidio.input.core.services.transformation.TransformationServiceImpl;
import presidio.input.core.services.transformation.managers.*;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.sdk.impl.spring.OutputDataServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan()
@Import({PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class, OutputDataServiceConfig.class, PresidioMonitoringConfiguration.class, ElasticsearchConfig.class})
public class InputCoreConfiguration {

    private static final Logger logger = Logger.getLogger(InputCoreConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${operation.type.category.mapping.file.path}")
    private String operationTypeCategoryMappingFilePath;

    @Value("${operation.type.category.hierarchy.mapping.file.path}")
    private String operationTypeCategoryHierarchyMappingFilePath;

    public Map<Schema, Map<String, List<String>>> getMapping(String filePath) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<Schema, Map<String, List<String>>>> mapping;
        try {
            Resource resource = applicationContext.getResources(filePath)[0];
            mapping = mapper.readValue(resource.getFile(), new TypeReference<Map<String, Map<Schema, Map<String, List<String>>>>>() {
            });
            return mapping.get("mapping");
        } catch (IOException e) {
            logger.error("error loading the {} mapping file", filePath, e);
            return Collections.emptyMap();
        }
    }

    @Autowired
    private PresidioInputPersistencyService presidioInputPersistencyService;

    @Autowired
    private AdeDataService adeDataService;

    @Autowired
    private OutputDataServiceSDK outputDataServiceSDK;

    @Bean
    public TransformationService transformationService() {
        return new TransformationServiceImpl();
    }

    @Bean
    public ConverterService converterService() {
        return new ConverterServiceImpl();
    }

    @Bean
    public PresidioExecutionService inputExecutionService() {
        return new InputExecutionServiceImpl(presidioInputPersistencyService, inputCoreManager());
    }

    @Bean
    public InputCoreManager inputCoreManager() {
        return new InputCoreManager(presidioInputPersistencyService, adeDataService, outputDataServiceSDK, transformationService(), converterService());
    }

    @Bean
    public FactoryBean serviceLocatorFactoryBean() {
        ServiceLocatorFactoryBean factoryBean = new ServiceLocatorFactoryBean();
        factoryBean.setServiceLocatorInterface(SchemaFactory.class);
        return factoryBean;
    }

    @Bean(name = "ACTIVE_DIRECTORY.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ActiveDirectoryTransformationManager activeDirectoryTransformationManager() {
        return new ActiveDirectoryTransformationManager(getMapping(operationTypeCategoryMappingFilePath), getMapping(operationTypeCategoryHierarchyMappingFilePath));
    }

    @Bean(name = "AUTHENTICATION.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuthenticationTransformerManager authenticationTransformerManager() {
        return new AuthenticationTransformerManager(getMapping(operationTypeCategoryMappingFilePath));
    }

    @Bean(name = "FILE.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileTransformerManager fileTransformerManager() {
        return new FileTransformerManager(getMapping(operationTypeCategoryMappingFilePath));
    }

    @Bean(name = "PRINT.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrintTransformerManager printTransformerManager() {
        return new PrintTransformerManager();
    }

    @Bean(name = "FILE.input-output-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileInputToOutputConverter fileInputToOutputConverter() {
        return new FileInputToOutputConverter();
    }

    @Bean(name = "ACTIVE_DIRECTORY.input-output-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ActiveDirectoryInputToOutputConverter activeDirectoryInputToOutputConverter() {
        return new ActiveDirectoryInputToOutputConverter();
    }

    @Bean(name = "AUTHENTICATION.input-output-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuthenticationInputToOutputConverter authenticationInputToOutputConverter() {
        return new AuthenticationInputToOutputConverter();
    }

    @Bean(name = "PRINT.input-output-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrintInputToOutputConverter printeInputToOutputConverter() {
        return new PrintInputToOutputConverter();
    }

    @Bean(name = "FILE.input-ade-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileInputToAdeConverter fileInputToAdeConverter() {
        return new FileInputToAdeConverter();
    }

    @Bean(name = "ACTIVE_DIRECTORY.input-ade-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ActiveDirectoryInputToAdeConverter activeDirectoryInputToAdeConverter() {
        return new ActiveDirectoryInputToAdeConverter();
    }

    @Bean(name = "AUTHENTICATION.input-ade-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuthenticationInputToAdeConverter authenticationInputToAdeConverter() {
        return new AuthenticationInputToAdeConverter();
    }

    @Bean(name = "PRINT.input-ade-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PrintInputToAdeConverter printInputToAdeConverter() {
        return new PrintInputToAdeConverter();
    }

    @Bean(name = "IOC.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IocTransformerManager IocTransformerManager() {
        return new IocTransformerManager();
    }

    @Bean(name = "IOC.input-output-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IocInputToOutputConverter iocInputToOutputConverter() {
        return new IocInputToOutputConverter();
    }

    @Bean(name = "IOC.input-ade-converter")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IocInputToAdeConverter iocInputToAdeConverter() {
        return new IocInputToAdeConverter();
    }
}
