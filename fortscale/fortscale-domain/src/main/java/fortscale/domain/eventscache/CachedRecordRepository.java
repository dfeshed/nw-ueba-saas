package fortscale.domain.eventscache;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CachedRecordRepository  extends MongoRepository<CachedRecord, String>, CachedRecordRepositoryCustom {

	List<CachedRecord> findByCacheName(String cacheName);
}
