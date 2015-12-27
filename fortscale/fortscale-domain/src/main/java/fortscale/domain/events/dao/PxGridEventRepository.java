package fortscale.domain.events.dao;

import fortscale.domain.events.IseEvent;
import fortscale.domain.events.PxGridIPEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PxGridEventRepository extends MongoRepository<PxGridIPEvent, String> {
    public List<PxGridIPEvent> findByIpaddressAndTimestampepochLessThan(String ipaddress, Long timstamp, Pageable pageable);

    public List<PxGridIPEvent> findByIpaddressAndTimestampepochGreaterThanEqual(String ipaddress, Long timstamp,
            Pageable pageable);

    public List<PxGridIPEvent> findByIpaddress(String ipaddress, Pageable pageable);
}
