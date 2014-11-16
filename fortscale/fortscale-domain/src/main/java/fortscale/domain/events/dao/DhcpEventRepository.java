package fortscale.domain.events.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.events.DhcpEvent;


public interface DhcpEventRepository  extends MongoRepository<DhcpEvent, String>, DhcpEventRepositoryCustom {
	public List<DhcpEvent> findByIpaddressAndTimestampepochBetween(String ipaddress, Long minTimestampepoch, Long maxTimestampepoch, Pageable pageable);
}

