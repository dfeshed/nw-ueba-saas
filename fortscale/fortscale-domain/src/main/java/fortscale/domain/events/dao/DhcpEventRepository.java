package fortscale.domain.events.dao;

import fortscale.domain.events.DhcpEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface DhcpEventRepository  extends MongoRepository<DhcpEvent, String> {
	public List<DhcpEvent> findByIpaddressAndTimestampepochLessThan(String ipaddress, Long timstamp, Pageable pageable);

	public List<DhcpEvent> findByIpaddressAndTimestampepochGreaterThanEqual(String ipaddress, Long timstamp, Pageable pageable);
}

