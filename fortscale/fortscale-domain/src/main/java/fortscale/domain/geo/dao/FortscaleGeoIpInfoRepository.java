package fortscale.domain.geo.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import fortscale.domain.geo.FortscaleGeoIpInfo;

public interface FortscaleGeoIpInfoRepository extends MongoRepository<FortscaleGeoIpInfo,String> {
	public FortscaleGeoIpInfo findByIp(String ip);
}
