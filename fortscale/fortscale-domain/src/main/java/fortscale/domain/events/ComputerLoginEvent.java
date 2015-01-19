package fortscale.domain.events;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection=ComputerLoginEvent.collectionName)
@CompoundIndexes({
	@CompoundIndex(name="ipaddressTimeIdx", def = "{'ipaddress': 1, 'timestampepoch': -1}")
})
public class ComputerLoginEvent extends IpToHostname{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8490137402047557595L;
	public static final String collectionName =  "ComputerLogin";
	
	
}
