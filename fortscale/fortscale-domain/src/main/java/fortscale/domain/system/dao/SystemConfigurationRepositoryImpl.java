package fortscale.domain.system.dao;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import fortscale.domain.system.DcSystemConfiguraion;
import fortscale.domain.system.SystemConfiguration;
import fortscale.domain.system.SystemConfigurationEnum;

public class SystemConfigurationRepositoryImpl implements SystemConfigurationRepositoryCustom{
	
	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public DcSystemConfiguraion findDcConfiguration() {
		Query query = new Query(where(SystemConfiguration.typeFieldName).is(SystemConfigurationEnum.dc));
		return mongoTemplate.findOne(query, DcSystemConfiguraion.class);
	}

}
