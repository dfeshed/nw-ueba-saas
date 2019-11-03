package presidio.output.processor.services.entity;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Created by shays on 27/08/2017.
 */
public interface EntityScoreService {

    void clearEntityScoreForEntitiesThatShouldNotHaveScore(Set<String> excludedEntitiesIds, String entityType);

    Map<String, EntitiesAlertData> calculateEntityAlertsData(int alertEffectiveDurationInDays, Instant endDate, String entityType);

    Map<String, Double> calculateEntityScores(Instant startDate, Instant endDate, String entityType);

}
