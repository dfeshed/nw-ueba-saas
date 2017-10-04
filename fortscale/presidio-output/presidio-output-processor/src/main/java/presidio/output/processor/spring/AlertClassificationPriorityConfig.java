package presidio.output.processor.spring;

import fortscale.utils.spring.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import presidio.output.processor.config.ClassificationPriorityConfig;
import presidio.output.processor.config.DataConfig;
import presidio.output.processor.config.SupportingInformationConfig;
import presidio.output.processor.services.alert.AlertClassificationService;
import presidio.output.processor.services.alert.AlertClassificationServiceImpl;


public class AlertClassificationPriorityConfig extends ApplicationConfiguration {

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
        return bindPropertiesToTarget(DataConfig.class, null, "classpath:supporting_information_config.yml");
    }

    @Value("${number.of.classifications}")
    private int numberOfClassifications;


}
