package presidio.output.processor.services.alert;


import org.apache.commons.lang.StringUtils;
import presidio.output.domain.records.alerts.ClassificationPriority;
import presidio.output.processor.config.ClassificationPriorityConfig;
import presidio.output.processor.config.IndicatorConfig;
import presidio.output.processor.config.SupportingInformationConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlertClassificationServiceImpl implements AlertClassificationService {

    private Map<String, ClassificationPriority> indicatorToAlert;
    private ClassificationPriorityConfig classificationPriorityConfig;
    private SupportingInformationConfig supportingInformationConfig;


    public AlertClassificationServiceImpl(ClassificationPriorityConfig classificationPriorityConfig, SupportingInformationConfig supportingInformationConfig) {
        this.classificationPriorityConfig = classificationPriorityConfig;
        this.supportingInformationConfig = supportingInformationConfig;
        createIndicatorToAlertByPriority();

    }

    private void createIndicatorToAlertByPriority() {
        indicatorToAlert = new HashMap<>();
        String classification, indicatorName;
        int priority, number = 0;
        List<IndicatorConfig> indicatorConfigs = supportingInformationConfig.getIndicators();
        for (IndicatorConfig indicatorConfig : indicatorConfigs) {
            number++;
            indicatorName = indicatorConfig.getName();
            classification = indicatorConfig.getClassification();
            priority = classificationPriorityConfig.getClassificationConfig(classification).getPriority();
            this.indicatorToAlert.put(indicatorName, new ClassificationPriority(classification, priority));
        }
    }

    public List<String> getAlertClassificationsFromIndicatorsByPriority(List<String> indicators) {
        int priority, place, remove, numberOfIndicators;
        List<String> classificationByPriority = new ArrayList<>();
        Set<String> tempClassificationByPriority = new HashSet<>();
        String alertName;
        ClassificationPriority classificationPriority;
        numberOfIndicators = indicators.size();
        for (int i = 0; i < numberOfIndicators; i++) {
            priority = indicatorToAlert.size() + 1;
            alertName = "";
            place = 0;
            remove = 0;
            for (String indicator : indicators) {
                classificationPriority = indicatorToAlert.get(indicator);
                if (classificationPriority != null && classificationPriority.getPriority() < priority) {
                    priority = classificationPriority.getPriority();
                    alertName = classificationPriority.getName();
                    remove = place;
                }
                place++;
            }
            indicators.remove(remove);
            if (StringUtils.isNotEmpty(alertName))
                tempClassificationByPriority.add(alertName);
        }
        classificationByPriority.addAll(tempClassificationByPriority);
        return classificationByPriority;
    }

}
