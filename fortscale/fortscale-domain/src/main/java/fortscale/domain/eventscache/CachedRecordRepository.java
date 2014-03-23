package fortscale.domain.eventscache;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface CachedRecordRepository  extends MongoRepository<CachedRecord, Long>, CachedRecordRepositoryCustom {

	List<CachedRecord> findByCacheName(String cacheName);
}
