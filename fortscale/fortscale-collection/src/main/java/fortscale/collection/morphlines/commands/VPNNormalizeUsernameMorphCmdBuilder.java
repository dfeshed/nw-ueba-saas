package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.services.impl.UsernameNormalizer;

@Deprecated
@Configurable(preConstruction=true)
public class VPNNormalizeUsernameMorphCmdBuilder extends	NormalizeUsernameMorphCmdBuilder {

	@Autowired
	UsernameNormalizer vpnUsernameNormalizer;
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("VPNNormalizeUsername");
	}
	
	@Override
	protected UsernameNormalizer getUsernameNormalizer(){
		return vpnUsernameNormalizer;
	}
}
