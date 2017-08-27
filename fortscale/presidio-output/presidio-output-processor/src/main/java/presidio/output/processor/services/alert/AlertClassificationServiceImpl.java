package presidio.output.processor.services.alert;


import presidio.output.domain.records.alerts.ClassificationPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlertClassificationServiceImpl implements AlertClassificationService{

    private final String COMA = ",";
    private final String LOWER_LINE = "_";
    private final String SPACE = "";

    private Map<String, ClassificationPriority> indicatorToAlert;


    public AlertClassificationServiceImpl(String classifications, String indicators, String classificationsByPriority) {
        indicatorToAlert = new HashMap<>();
        createIndicatorToAlertByPriority(classifications, indicators, setAlertsByPriority(classificationsByPriority));

    }

    private List<String> setAlertsByPriority(String classificationsByPriority) {
        //TODO: setting the indicators from json and not string
        String[] names = classificationsByPriority.replace(LOWER_LINE, SPACE).split(COMA);
        List<String> classifications = new ArrayList<>();
        for (String name : names) {
            classifications.add(name);
        }
        return classifications;
    }

    private void createIndicatorToAlertByPriority(String alerts, String indicators, List<String> classificationsByPriority) {
        String[] indicatorsNames = indicators.replace(LOWER_LINE, SPACE).split(COMA);
        String[] classificationsNames = alerts.replace(LOWER_LINE, SPACE).split(COMA);
        String alertName, indicatorName;
        for (int i = 0; i < indicatorsNames.length; i++) {
            indicatorName = indicatorsNames[i];
            alertName = classificationsNames[i];
            this.indicatorToAlert.put(indicatorName, new ClassificationPriority(alertName, classificationsByPriority.indexOf(alertName)));
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
                if (classificationPriority !=null && classificationPriority.getPriority() < priority) {
                    priority = classificationPriority.getPriority();
                    alertName = classificationPriority.getName();
                    remove = place;
                }
                place++;
            }
            indicators.remove(remove);
            tempClassificationByPriority.add(alertName);
        }
        classificationByPriority.addAll(tempClassificationByPriority);
        return classificationByPriority;
    }

}
