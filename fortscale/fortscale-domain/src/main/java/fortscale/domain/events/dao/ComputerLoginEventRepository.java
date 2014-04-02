package fortscale.domain.events.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.events.ComputerLoginEvent;

public interface ComputerLoginEventRepository extends MongoRepository<ComputerLoginEvent, String>{

	public List<ComputerLoginEvent> findByIpaddressAndTimestampepochGreaterThanEqualAndTimestampepochLessThanEqual(String ipaddress, Long minTimestampepoch, Long maxTimestampepoch, Pageable pageable);
}
