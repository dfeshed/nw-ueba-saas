package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.events.EnrichedEvent;
import presidio.output.domain.services.event.EventPersistencyService;
import presidio.output.domain.translator.OutputToCollectionNameTranslator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


public class EntityPropertiesUpdateServiceImpl implements EntityPropertiesUpdateService {
    private static final Logger log = Logger.getLogger(EntityPropertiesUpdateServiceImpl.class);

    private final EventPersistencyService eventPersistencyService;
    private final OutputToCollectionNameTranslator outputToCollectionNameTranslator;


    public EntityPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService, OutputToCollectionNameTranslator outputToCollectionNameTranslator) {
        this.eventPersistencyService = eventPersistencyService;
        this.outputToCollectionNameTranslator = outputToCollectionNameTranslator;
    }

    /**
     * Searching latest event of this entity in all schemas and update this entity properties if needed.
     *
     * @param entity - Entity object that maybe need to update properties
     * @return Entity with updated properties or null if there is no change.
     */
    @Override
    public Entity updateEntityProperties(Entity entity) {
        boolean isUpdated = false;
        List<String> collectionNames = collectionNamesByOrderForEvents();
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForEntity(entity.getEntityId(), collectionNames, entity.getEntityType());
        if (enrichedEvent != null) {
            if (!Objects.equals(entity.getEntityName(), enrichedEvent.getUserName())) {
                entity.setEntityName(enrichedEvent.getUserName());
                isUpdated = true;
            }
        } else {
            log.debug("No events whereAdeManagerSdkImpl.java found for this entity , therefore cannot update entity properties accordingly to latest event");
        }
        if (isUpdated) {
            return entity;
        } else {
            return null;
        }
    }

    @Override
    public List<String> collectionNamesByOrderForEvents() {
        List<Schema> schemas = Arrays.asList(Schema.values());
        List<String> collections = new ArrayList<>();
        schemas.forEach(schema -> {
            collections.add(outputToCollectionNameTranslator.toCollectionName(schema));
        });
        return collections;
    }
}
