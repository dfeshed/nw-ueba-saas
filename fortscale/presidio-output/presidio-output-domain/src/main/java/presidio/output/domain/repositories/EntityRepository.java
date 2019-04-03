package presidio.output.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import presidio.output.domain.records.entity.Entity;

import java.util.Collection;
import java.util.stream.Stream;

public interface EntityRepository extends ElasticsearchRepository<Entity, String> {
    Page<Entity> findByUserName(String userName, Pageable pageable);

    Page<Entity> findByUserId(String userId, Pageable pageable);

    Page<Entity> findByIdIn(Collection<String> ids, Pageable pageable);

    Stream<Entity> findByUpdatedByLogicalStartDateGreaterThanEqualAndUpdatedByLogicalEndDateLessThanEqual(long startDate, long endDate); // the stream must be closed after usage

}
