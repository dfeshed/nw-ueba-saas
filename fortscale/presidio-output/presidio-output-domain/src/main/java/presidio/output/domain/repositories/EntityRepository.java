package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.entity.Entity;

import java.util.Collection;
import java.util.stream.Stream;

public interface EntityRepository extends ElasticsearchRepository<Entity, String> {
    Page<Entity> findByEntityName(String entityName, Pageable pageable);

    Page<Entity> findByEntityId(String entityId, Pageable pageable);

    Page<Entity> findByIdIn(Collection<String> ids, Pageable pageable);

    Stream<Entity> findByLastUpdateLogicalStartDateGreaterThanEqualAndLastUpdateLogicalEndDateLessThanEqualAndEntityType(long startDate, long endDate, String entityType); // the stream must be closed after usage

}
