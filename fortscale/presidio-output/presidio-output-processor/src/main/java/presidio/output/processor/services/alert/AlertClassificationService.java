package presidio.output.processor.services.alert;

import java.util.List;

public interface AlertClassificationService {

    /**
     * creating list of classifications sorted by priority from list of indicators
     * @param indicators - list fo indicators from smart
     * @return list of classifications
     */
    List<String> getAlertClassificationsFromIndicatorsByPriority(List<String> indicators);
}
