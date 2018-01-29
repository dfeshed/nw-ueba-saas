package presidio.input.core.spring;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.common.general.Schema;
import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.elasticsearch.config.ElasticsearchConfig;
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
import presidio.input.core.services.converters.ade.ActiveDirectoryInputToAdeConverter;
import presidio.input.core.services.converters.ade.AuthenticationInputToAdeConverter;
import presidio.input.core.services.converters.ade.FileInputToAdeConverter;
import presidio.input.core.services.converters.ade.PrintInputToAdeConverter;
import presidio.input.core.services.converters.output.ActiveDirectoryInputToOutputConverter;
import presidio.input.core.services.converters.output.AuthenticationInputToOutputConverter;
import presidio.input.core.services.converters.output.FileInputToOutputConverter;
import presidio.input.core.services.converters.output.PrintInputToOutputConverter;
import presidio.input.core.services.data.AdeDataService;
import presidio.input.core.services.impl.InputCoreManager;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.services.impl.SchemaFactory;
import presidio.input.core.services.transformation.managers.*;
import presidio.input.sdk.impl.spring.PresidioInputPersistencyServiceConfig;
import presidio.monitoring.spring.PresidioMonitoringConfiguration;
import presidio.output.sdk.api.OutputDataServiceSDK;
import presidio.output.sdk.impl.spring.OutputDataServiceConfig;
import presidio.sdk.api.services.PresidioInputPersistencyService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ComponentScan()
@Import({PresidioInputPersistencyServiceConfig.class, AdeDataServiceConfig.class, OutputDataServiceConfig.class, PresidioMonitoringConfiguration.class, ElasticsearchConfig.class})
public class InputCoreConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Value("${operation.type.category.mapping.file.path}")
    private String operationTypeCategoryMappingFilePath;

    @Bean
    public Map<Schema, Map<String, List<String>>> getOperationTypeToCategoryMapping() {
        ObjectMapper mapper = new ObjectMapper();
        Map operationTypeToCategoryMapping = new HashMap();
        try {
            Resource resource = applicationContext.getResources(operationTypeCategoryMappingFilePath)[0];
            operationTypeToCategoryMapping = mapper.readValue(resource.getFile(), Map.class);
            return (Map<Schema, Map<String, List<String>>>) operationTypeToCategoryMapping.get("mapping");
        } catch (IOException e) {
            e.printStackTrace();
            return operationTypeToCategoryMapping;
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
        return new ActiveDirectoryTransformationManager(getOperationTypeToCategoryMapping());
    }

    @Bean(name = "AUTHENTICATION.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public AuthenticationTransformerManager authenticationTransformerManager() {
        return new AuthenticationTransformerManager(getOperationTypeToCategoryMapping());
    }

    @Bean(name = "FILE.transformer")
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public FileTransformerManager fileTransformerManager() {
        return new FileTransformerManager(getOperationTypeToCategoryMapping());
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
}
