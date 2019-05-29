package presidio.output.commons.services.entity;

import java.util.List;

/**
 * Created by Efrat Noam on 12/4/17.
 */
public interface EntitySeverityService {

    EntitySeverityServiceImpl.EntityScoreToSeverity getSeveritiesMap(boolean recalcEntityScorePercentiles);

    /**
     * Iterate all entities and re-calculate the severities percentiles - read entities from DB and update severities in DB
     */
    void updateSeverities();

    List<String> collectionNamesByOrderForEvents();
}
