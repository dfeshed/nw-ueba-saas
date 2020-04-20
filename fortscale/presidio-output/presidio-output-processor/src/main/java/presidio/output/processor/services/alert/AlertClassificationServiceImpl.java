package presidio.output.processor.services.alert;


import org.apache.commons.lang.StringUtils;
import presidio.output.domain.records.alerts.ClassificationPriority;
import presidio.output.processor.config.ClassificationPriorityConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlertClassificationServiceImpl implements AlertClassificationService {

    private Map<String, ClassificationPriority> indicatorToAlert;
    private ClassificationPriorityConfig classificationPriorityConfig;
    private SupportingInformationConfig supportingInformationConfig;
    private int numberOfClassifications;


    public AlertClassificationServiceImpl(ClassificationPriorityConfig classificationPriorityConfig, SupportingInformationConfig supportingInformationConfig, int numberOfClassifications) {
        this.classificationPriorityConfig = classificationPriorityConfig;
        this.supportingInformationConfig = supportingInformationConfig;
        this.numberOfClassifications = numberOfClassifications;
        createIndicatorToAlertByPriority();

    }

    private void createIndicatorToAlertByPriority() {
        indicatorToAlert = new HashMap<>();
        String classification, indicatorName;
        int priority;
        List<IndicatorConfig> indicatorConfigs = supportingInformationConfig.getIndicators();
        for (IndicatorConfig indicatorConfig : indicatorConfigs) {
            indicatorName = indicatorConfig.getName();
            classification = indicatorConfig.getClassification();
            priority = classificationPriorityConfig.getClassificationConfig(classification).getPriority();
            this.indicatorToAlert.put(indicatorName, new ClassificationPriority(classification, priority));
        }
    }

    public List<String> getAlertClassificationsFromIndicatorsByPriority(List<String> indicators) {

        Set<String> classificationByPriority = new LinkedHashSet<>();
        String[] classifications = new String[numberOfClassifications];
        indicators.forEach(indicator -> {
            classifications[indicatorToAlert.get(indicator).getPriority() - 1] = indicatorToAlert.get(indicator).getName();
        });

        for (int i = 0; i < classifications.length; i++) {
            if (StringUtils.isNotEmpty(classifications[i]))
                classificationByPriority.add(classifications[i]);
        }
        return new ArrayList<>(classificationByPriority);
    }

}
