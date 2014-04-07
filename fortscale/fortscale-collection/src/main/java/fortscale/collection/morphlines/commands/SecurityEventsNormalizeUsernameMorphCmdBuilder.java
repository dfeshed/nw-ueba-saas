package fortscale.collection.morphlines.commands;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import fortscale.services.impl.UsernameNormalizer;

@Configurable(preConstruction=true)
public class SecurityEventsNormalizeUsernameMorphCmdBuilder extends	NormalizeUsernameMorphCmdBuilder {

	@Autowired
	UsernameNormalizer secUsernameNormalizer;
	
	@Override
	public Collection<String> getNames() {
		return Collections.singletonList("SECNormalizeUsername");
	}
	
	@Override
	protected UsernameNormalizer getUsernameNormalizer(){
		return secUsernameNormalizer;
	}
}
