package presidio.output.commons.services.entity;


import presidio.output.domain.records.entity.Entity;

import java.util.List;


public interface EntityPropertiesUpdateService {

    Entity entityPropertiesUpdate(Entity entity);

    List<String> collectionNamesByOrderForEvents();
}
