package presidio.output.processor.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import presidio.output.processor.config.ClassificationPriorityConfig;
import presidio.output.processor.config.DataConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.services.alert.AlertClassificationServiceImpl;
import presidio.output.processor.services.alert.indicator.enricher.IndicatorEnricher;
import presidio.output.processor.services.alert.indicator.enricher.IndicatorEnricherJsonDeserializer;

import java.io.IOException;
import java.io.InputStream;

public class AlertClassificationPriorityConfig {
    @Value("${number.of.classifications}")
    private int numberOfClassifications;

    @Value("${supporting.information.config.resource:classpath:supporting_information_config.yml}")
    private Resource supportingInformationConfigResource;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public AlertClassificationService AlertClassificationService() {
        return new AlertClassificationServiceImpl(classificationPriorityConfig(), supportingInformationConfig(), numberOfClassifications);
    }

    @Bean
    public SupportingInformationConfig supportingInformationConfig() {
        return new SupportingInformationConfig(dataConfig().getIndicators());
    }

    @Bean
    public ClassificationPriorityConfig classificationPriorityConfig() {
        return new ClassificationPriorityConfig(dataConfig().getClassificationPriority());
    }

    @Bean
    public DataConfig dataConfig() {
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        objectMapper.registerModule(new SimpleModule().addDeserializer(
                IndicatorEnricher.class,
                new IndicatorEnricherJsonDeserializer(objectMapper, applicationContext)
        ));

        try {
            InputStream inputStream = supportingInformationConfigResource.getInputStream();
            return objectMapper.readValue(inputStream, DataConfig.class);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }
}
