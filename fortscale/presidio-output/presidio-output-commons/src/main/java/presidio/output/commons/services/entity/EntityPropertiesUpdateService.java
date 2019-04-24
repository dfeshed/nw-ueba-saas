package presidio.output.commons.services.entity;


import presidio.output.domain.records.entity.Entity;

import java.util.List;


public interface EntityPropertiesUpdateService {

    Entity updateEntityProperties(Entity entity);

    List<String> collectionNamesByOrderForEvents();
}
