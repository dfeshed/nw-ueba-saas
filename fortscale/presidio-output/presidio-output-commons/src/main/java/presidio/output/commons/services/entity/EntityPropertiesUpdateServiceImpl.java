package presidio.output.commons.services.entity;

import fortscale.common.general.Schema;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.ObjectUtils;
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
    private final String TAG_ADMIN = "admin";


    public EntityPropertiesUpdateServiceImpl(EventPersistencyService eventPersistencyService, OutputToCollectionNameTranslator outputToCollectionNameTranslator) {
        this.eventPersistencyService = eventPersistencyService;
        this.outputToCollectionNameTranslator = outputToCollectionNameTranslator;
    }

    /**
     * When trying to update entity properties we search for the latest AuthenticationEnrichedEven if exist, if not we
     * search for FileEnrichedEvent if exist, if not we search for ActiveDirectoryEnrichedEvent.
     * If the entity properties have change we return the updated entity if not we return null.
     *
     * @param entity - Entity object that maybe need to update properties
     * @return Entity with updated properties or null if there is no change.
     */
    @Override
    public Entity entityPropertiesUpdate(Entity entity) {
        boolean isUpdated = false;
        List<String> collectionNames = collectionNamesByOrderForEvents();
        EnrichedEvent enrichedEvent = eventPersistencyService.findLatestEventForEntity(entity.getEntityId(), collectionNames);
        if (!ObjectUtils.isEmpty(enrichedEvent)) {
//            if (!Objects.equals(entity.getUserDisplayName(), enrichedEvent.getUserDisplayName())) {
//                entity.setUserDisplayName(enrichedEvent.getUserDisplayName());
//                isUpdated = true;
//            }
            if (!Objects.equals(entity.getEntityId(), enrichedEvent.getUserId()) && !StringUtils.isEmpty(enrichedEvent.getUserId())) {
                entity.setEntityId(enrichedEvent.getUserId());
                isUpdated = true;
            }
            if (!Objects.equals(entity.getEntityName(), enrichedEvent.getUserName())) {
                entity.setEntityName(enrichedEvent.getUserName());
//                entity.setUserDisplayNameSortLowercase(enrichedEvent.getEntityName());
//                entity.setIndexedEntityName(enrichedEvent.getEntityName());
                isUpdated = true;
            }
            List<String> entityTags = entity.getTags();

            boolean isAdmin = false;
            if (MapUtils.isNotEmpty(enrichedEvent.getAdditionalInfo()) && StringUtils.isNotEmpty(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))
                    && Boolean.parseBoolean(enrichedEvent.getAdditionalInfo().get(EnrichedEvent.IS_USER_ADMIN))) {
                isAdmin = true;
            }

            // If the entity marked as admin but the last event arrived without the admin tag -> remove the admin tag
            if (CollectionUtils.isNotEmpty(entityTags) && entityTags.contains(TAG_ADMIN) && !isAdmin) {
                entityTags.remove(TAG_ADMIN);
                isUpdated = true;
            } else if (isAdmin) {
                if (entityTags == null) {
                    entityTags = new ArrayList<>();
                    entity.setTags(entityTags);
                }
                // If the entity wasn't as admin but the last event arrived with the admin tag -> add the admin tag
                if (!entityTags.contains(TAG_ADMIN)) {
                    entityTags.add(TAG_ADMIN);
                    isUpdated = true;
                }
            }

        } else {
            log.debug("No events where found for this entity , therefore cannot update entity properties accordingly to latest event");
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
