package fortscale.domain.events.dao;

import fortscale.domain.events.ComputerLoginEvent;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComputerLoginEventRepository extends MongoRepository<ComputerLoginEvent, String>,ComputerLoginEventRepositoryCustom{

	public List<ComputerLoginEvent> findByIpaddressAndTimestampepochBetween(String ipaddress, Long minTimestampepoch, Long maxTimestampepoch, Pageable pageable);

	public List<ComputerLoginEvent> findByIpaddressAndTimestampepochGreaterThanEqual(String ipaddress, Long timestampepoch, Pageable pageable);

	public ComputerLoginEvent findByIpaddressAndTimestampepoch(String ipaddress, Long timestampepoch);
}
