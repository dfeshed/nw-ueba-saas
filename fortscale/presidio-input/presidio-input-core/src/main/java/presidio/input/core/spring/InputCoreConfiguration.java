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
    @Lazy
    public ActiveDirectoryTransformationManager activeDirectoryTransformationManager() {
        return new ActiveDirectoryTransformationManager(getMapping(operationTypeCategoryMappingFilePath), getMapping(operationTypeCategoryHierarchyMappingFilePath));
    }

    @Bean(name = "AUTHENTICATION.transformer")
    @Lazy
    public AuthenticationTransformerManager authenticationTransformerManager() {
        return new AuthenticationTransformerManager(getMapping(operationTypeCategoryMappingFilePath));
    }

    @Bean(name = "FILE.transformer")
    @Lazy
    public FileTransformerManager fileTransformerManager() {
        return new FileTransformerManager(getMapping(operationTypeCategoryMappingFilePath));
    }

    @Bean(name = "REGISTRY.transformer")
    @Lazy
    public RegistryTransformerManager registryTransformerManager() {
        return new RegistryTransformerManager();
    }

    @Bean(name = "PRINT.transformer")
    @Lazy
    public PrintTransformerManager printTransformerManager() {
        return new PrintTransformerManager();
    }

    @Bean(name = "PROCESS.transformer")
    @Lazy
    public ProcessTransformerManager processTransformerManager() {
        return new ProcessTransformerManager();
    }

    @Bean(name = "IOC.transformer")
    @Lazy
    public IocTransformerManager iocTransformerManager() {
        return new IocTransformerManager();
    }

    @Bean(name = "FILE.input-output-converter")
    @Lazy
    public FileInputToOutputConverter fileInputToOutputConverter() {
        return new FileInputToOutputConverter();
    }

    @Bean(name = "ACTIVE_DIRECTORY.input-output-converter")
    @Lazy
    public ActiveDirectoryInputToOutputConverter activeDirectoryInputToOutputConverter() {
        return new ActiveDirectoryInputToOutputConverter();
    }

    @Bean(name = "AUTHENTICATION.input-output-converter")
    @Lazy
    public AuthenticationInputToOutputConverter authenticationInputToOutputConverter() {
        return new AuthenticationInputToOutputConverter();
    }

    @Bean(name = "PROCESS.input-output-converter")
    @Lazy
    public ProcessInputToOutputConverter processInputToOutputConverter() {
        return new ProcessInputToOutputConverter();
    }

    @Bean(name = "PRINT.input-output-converter")
    @Lazy
    public PrintInputToOutputConverter printeInputToOutputConverter() {
        return new PrintInputToOutputConverter();
    }

    @Bean(name = "REGISTRY.input-output-converter")
    @Lazy
    public RegistryInputToOutputConverter registryInputToOutputConverter() {
        return new RegistryInputToOutputConverter();
    }

    @Bean(name = "IOC.input-output-converter")
    @Lazy
    public IocInputToOutputConverter iocInputToOutputConverter() {
        return new IocInputToOutputConverter();
    }

    @Bean(name = "FILE.input-ade-converter")
    @Lazy
    public FileInputToAdeConverter fileInputToAdeConverter() {
        return new FileInputToAdeConverter();
    }

    @Bean(name = "ACTIVE_DIRECTORY.input-ade-converter")
    @Lazy
    public ActiveDirectoryInputToAdeConverter activeDirectoryInputToAdeConverter() {
        return new ActiveDirectoryInputToAdeConverter();
    }

    @Bean(name = "AUTHENTICATION.input-ade-converter")
    @Lazy
    public AuthenticationInputToAdeConverter authenticationInputToAdeConverter() {
        return new AuthenticationInputToAdeConverter();
    }

    @Bean(name = "PRINT.input-ade-converter")
    @Lazy
    public PrintInputToAdeConverter printInputToAdeConverter() {
        return new PrintInputToAdeConverter();
    }

    @Bean(name = "PROCESS.input-ade-converter")
    @Lazy
    public ProcessInputToAdeConverter processInputToAdeConverter() {
        return new ProcessInputToAdeConverter();
    }

    @Bean(name = "REGISTRY.input-ade-converter")
    @Lazy
    public RegistryInputToAdeConverter registryInputToAdeConverter() {
        return new RegistryInputToAdeConverter();
    }

    @Bean(name = "IOC.input-ade-converter")
    @Lazy
    public IocInputToAdeConverter iocInputToAdeConverter() {
        return new IocInputToAdeConverter();
    }
}
