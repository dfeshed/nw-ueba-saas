package fortscale.streaming.service.usernameNormalization;

import fortscale.services.impl.UsernameNormalizer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Date: 24/01/2015.
 */
public class SecUsernameNormalizationService extends UsernameNormalizationService {

	@Autowired
	UsernameNormalizer secUsernameNormalizer;

	@Override
	protected UsernameNormalizer getUsernameNormalizer(){
		return secUsernameNormalizer;
	}

}
