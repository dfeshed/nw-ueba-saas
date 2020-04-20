package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;

import java.util.List;

public interface EntityMappingService {

    List<Schema> getSchemas(String entityType);

    String getEntityNameField(String entityIdField);
}
