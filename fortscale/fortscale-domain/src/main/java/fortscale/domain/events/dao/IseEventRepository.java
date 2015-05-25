package fortscale.domain.events.dao;

import fortscale.domain.events.IseEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IseEventRepository extends MongoRepository<IseEvent, String> {
    public List<IseEvent> findByIpaddressAndTimestampepochLessThan(String ipaddress, Long timstamp, Pageable pageable);

    public List<IseEvent> findByIpaddressAndTimestampepochGreaterThanEqual(String ipaddress, Long timstamp, Pageable pageable);

    public List<IseEvent> findByIpaddress(String ipaddress, Pageable pageable);
}
