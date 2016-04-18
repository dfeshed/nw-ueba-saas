package fortscale.domain.events.dao;

import fortscale.domain.events.ComputerLoginEvent;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

/**
 * Created by idanp on 4/17/2016.
 */
public class ComputerLoginEventRepositoryImpl implements ComputerLoginEventRepositoryCustom {

	@Autowired
	private MongoTemplate mongoTemplate;

	@Override
	public void updateResolvingExpireDueToVPNSessionEnd(String ipAddress,long startSessionTime,long endSessionTime)
	{
		Update update = new Update();
		update.set(ComputerLoginEvent.PART_OF_VPN_FIELD, true);
		update.set(ComputerLoginEvent.EXPIRATION_VPN_SESSION_TIME_FIELD,endSessionTime);

		mongoTemplate.updateMulti(query(where(ComputerLoginEvent.IP_ADDRESS_FIELD_NAME).is(ipAddress).andOperator(Criteria.where(ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME).gte(TimestampUtils.convertToMilliSeconds(startSessionTime)), Criteria.where(ComputerLoginEvent.TIMESTAMP_EPOCH_FIELD_NAME).lte(TimestampUtils.convertToMilliSeconds(endSessionTime)))),update, ComputerLoginEvent.class);
	}
}
