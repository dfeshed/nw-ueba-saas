package presidio.output.domain.services.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface EntityPersistencyService {
    Entity save(Entity entity);

    Iterable<Entity> save(List<Entity> entities);

    Entity findEntityByDocumentId(String documentId);

    Iterable<Entity> findAll();

    Page<Entity> findByEntityId(String entityId, PageRequest pageRequest);

    Page<Entity> find(EntityQuery entityQuery);

    Page<Entity> findByIds(Collection<String> ids, PageRequest pageRequest);

    Stream<Entity> findEntitiesByLastUpdateLogicalDateAndEntityType(Instant startDate, Instant endDate, String entityType);
}
