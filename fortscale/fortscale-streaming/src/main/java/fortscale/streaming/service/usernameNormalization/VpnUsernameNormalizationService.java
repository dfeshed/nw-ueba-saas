
package fortscale.streaming.service.usernameNormalization;

import fortscale.services.impl.UsernameNormalizer;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Normalization service for VPN
 * Date: 24/01/2015.
 */
public class VpnUsernameNormalizationService extends UsernameNormalizationService {

	@Autowired
	UsernameNormalizer vpnUsernameNormalizer;

	@Override
	protected UsernameNormalizer getUsernameNormalizer(){
		return vpnUsernameNormalizer;
	}

}
