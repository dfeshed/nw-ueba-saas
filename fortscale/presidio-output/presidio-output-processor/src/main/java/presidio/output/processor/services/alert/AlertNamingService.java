package presidio.output.processor.services.alert;


import net.minidev.json.JSONObject;
import presidio.output.domain.records.alerts.AlertPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlertNamingService {

    private final String COMA = ",";
    private final String LOWER_LINE = "_";
    private final String SPACE = " ";

    private Map<String, AlertPriority> indicatorToAlert;


    public AlertNamingService(String alerts, String indicators, String alertsByPriority) {
        indicatorToAlert = new HashMap<>();
        List<String> list = setAlertsByPriority(alertsByPriority);
        createIndicatorToAlertByPriority(alerts, indicators, list);

    }

    private List<String> setAlertsByPriority(String alertsByPriority) {
        String[] names = alertsByPriority.replace(LOWER_LINE, SPACE).split(COMA);
        List<String> alerts = new ArrayList<>();
        for (String name : names) {
            alerts.add(name);
        }
        return alerts;
    }

    private void createIndicatorToAlertByPriority(String alerts, String indicators, List<String> alertsByPriority) {
        String[] indicatorsNames = indicators.replace(LOWER_LINE, SPACE).split(COMA);
        String[] alertsNames = alerts.replace(LOWER_LINE, SPACE).split(COMA);
        String alertName, indicatorName;
        for (int i = 0; i < indicatorsNames.length; i++) {
            indicatorName = indicatorsNames[i];
            alertName = alertsNames[i];
            this.indicatorToAlert.put(indicatorName, new AlertPriority(alertName, alertsByPriority.indexOf(alertName)));
        }
    }

    public List<String> alertNamesFromIndictorsByPriority(List<String> indicators) {
        int priority, place, remove, numberOfIndicators;
        List<String> list = new ArrayList<>();
        Set<String> set = new HashSet<>();
        String alertName;
        AlertPriority alertPriority;
        numberOfIndicators = indicators.size();
        for (int i = 0; i < numberOfIndicators; i++) {
            priority = indicatorToAlert.size() + 1;
            alertName = "";
            place = 0;
            remove = 0;
            for (String indicator : indicators) {
                alertPriority = indicatorToAlert.get(indicator);
                if (alertPriority.getPriority() < priority) {
                    priority = alertPriority.getPriority();
                    alertName = alertPriority.getName();
                    remove = place;
                }
                place++;
            }
            indicators.remove(remove);
            set.add(alertName);
        }
        list.addAll(set);
        return list;
    }

}
