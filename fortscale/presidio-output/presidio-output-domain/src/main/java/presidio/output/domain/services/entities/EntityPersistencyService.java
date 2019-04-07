package presidio.output.domain.services.entities;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

public interface EntityPersistencyService {
    Entity save(Entity entity);

    Iterable<Entity> save(List<Entity> entities);

    Entity findEntityById(String id);

    Iterable<Entity> findAll();

    Page<Entity> findByEntityName(String entityName, PageRequest pageRequest);

    Page<Entity> findByEntityId(String entityId, PageRequest pageRequest);

    Page<Entity> find(EntityQuery entityQuery);

    Page<Entity> findByIds(Collection<String> ids, PageRequest pageRequest);

    Stream<Entity> findEntitiesByUpdatedDate(Instant startDate, Instant endDate);
}
