package fortscale.streaming.serialization;


import fortscale.streaming.task.enrichment.UserInfoForUpdate;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by idanp on 2/3/2015.
 */
public class UserInfoUpdateSerdFactory implements SerdeFactory<UserInfoForUpdate> {

	@Override
	public Serde<UserInfoForUpdate> getSerde (String name, Config config)
	{
		return new GenericJacksonSerde<UserInfoForUpdate>(UserInfoForUpdate.class);
	}

}
