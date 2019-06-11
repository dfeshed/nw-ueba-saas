package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import presidio.output.domain.records.events.EnrichedEvent;

import java.util.List;

public interface EntityMappingService {

    List<Schema> getSchemas(String entityType);

    String getEntityName(EnrichedEvent event, String entityType);
}
