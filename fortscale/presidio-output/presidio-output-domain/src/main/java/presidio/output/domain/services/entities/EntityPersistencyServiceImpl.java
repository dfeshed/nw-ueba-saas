package presidio.output.domain.services.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import presidio.output.domain.records.entity.Entity;
import presidio.output.domain.records.entity.EntityQuery;
import presidio.output.domain.repositories.EntityRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class EntityPersistencyServiceImpl implements EntityPersistencyService {
    @Autowired
    private EntityRepository entityRepository;

    @Override
    public Entity save(Entity entity) {
        entity.updateFieldsBeforeSave();
        return entityRepository.save(entity);
    }

    @Override
    public Iterable<Entity> save(List<Entity> entities) {
        if (entities != null && entities.size() > 0) {
            entities.forEach(Entity::updateFieldsBeforeSave);
            return entityRepository.save(entities);
        } else {
            return Collections.EMPTY_LIST;
        }
    }

    @Override
    public Entity findEntityByDocumentId(String id) {
        return entityRepository.findOne(id);
    }

    @Override
    public Iterable<Entity> findAll() {
        return entityRepository.findAll();
    }

    /**
     * Finds by a collection of repository generated ids(elastic)
     */
    public Page<Entity> findByIds(Collection<String> ids, PageRequest pageRequest) {
        return entityRepository.findByIdIn(ids, pageRequest);
    }

    public Page<Entity> findByEntityId(String entityId, PageRequest pageRequest) {
        return entityRepository.findByEntityId(entityId, pageRequest);
    }

    @Override
    public Page<Entity> find(EntityQuery entityQuery) {
        return entityRepository.search(new EntityElasticsearchQueryBuilder(entityQuery).build());
    }
}
