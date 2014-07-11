package fortscale.streaming.serialization;


import fortscale.streaming.model.tagging.AccountMachineAccess;
import org.apache.samza.config.Config;
import org.apache.samza.serializers.Serde;
import org.apache.samza.serializers.SerdeFactory;

/**
 * Created by idanp on 7/7/2014.
 */
public class AccountStateSerdeFactory implements SerdeFactory<AccountMachineAccess> {

    @Override
    public Serde<AccountMachineAccess> getSerde (String name, Config config)
    {
        return new GenericJacksonSerde<AccountMachineAccess>(AccountMachineAccess.class);
    }
}
