package presidio.output.domain.services.alerts;


import presidio.output.domain.records.alerts.AlertPriority;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertNamingService {

    private final String COMA = ",";
    private final String LOWER_LINE = "_";
    private final String SPACE = " ";

    private Map<String, AlertPriority> indicatorToAlert;


    public AlertNamingService(String alerts, String indicators, String alertsPriority) {
        indicatorToAlert = new HashMap<>();
        List<String> list = alertsAndPriority(alertsPriority);
        createIndicatorToAlertByPriority(alerts, indicators, list);

    }

    private List<String> alertsAndPriority(String alertsByPriority) {
        String[] names = alertsByPriority.split(COMA);
        List<String> alerts = new ArrayList<>();
        for (String name : names) {
            alerts.add(name.replace(LOWER_LINE, SPACE));
        }
        return alerts;
    }

    private void createIndicatorToAlertByPriority(String alerts, String indicators, List<String> priority) {
        String[] indicatorsNames = indicators.split(COMA);
        String[] alertsNames = alerts.split(COMA);
        String alertName, indicatorName;
        for (int i = 0; i < indicatorsNames.length; i++) {
            indicatorName = indicatorsNames[i].replace(LOWER_LINE, SPACE);
            alertName = alertsNames[i].replace(LOWER_LINE, SPACE);
            this.indicatorToAlert.put(indicatorName, new AlertPriority(alertName, priority.indexOf(alertName)));
        }
    }

    public String alertNameFromIndictors(List<String> indicators) {
        int priority = indicatorToAlert.size();
        String alertName = "";
        AlertPriority alertPriority;
        for (String indicator : indicators) {
            alertPriority = indicatorToAlert.get(indicator);
            if (alertPriority.getPriority() < priority) {
                priority = alertPriority.getPriority();
                alertName = alertPriority.getName();
            }
        }
        return alertName;
    }

    public List<String> alertsFromIndictors(List<String> indicators) {
        List<String> alertName = new ArrayList<>();
        for (String indicator : indicators) {
            alertName.add(indicatorToAlert.get(indicator).getName());
        }
        return alertName;
    }
}
