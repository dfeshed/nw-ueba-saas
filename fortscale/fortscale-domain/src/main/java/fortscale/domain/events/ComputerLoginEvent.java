package fortscale.domain.events;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection=ComputerLoginEvent.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}"),
})
public class ComputerLoginEvent extends IpToHostname{
	public static final String collectionName =  "ComputerLogin";
	
	
}
