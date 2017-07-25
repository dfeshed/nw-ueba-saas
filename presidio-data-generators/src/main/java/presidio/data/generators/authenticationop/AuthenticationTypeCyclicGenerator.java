package presidio.data.generators.authenticationop;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class AuthenticationTypeCyclicGenerator extends AbstractCyclicValuesGenerator {

    private static final String[] DEFAULT_AUTHENTICATION_TYPE = {
            AUTHENTICATION_OPERATION_TYPE.NETWORK_OPERATION.value,
            AUTHENTICATION_OPERATION_TYPE.DOMAIN_OPERATION.value,
            AUTHENTICATION_OPERATION_TYPE.INTERACTIVE_OPERATION.value
    };

    public AuthenticationTypeCyclicGenerator() {
        super(DEFAULT_AUTHENTICATION_TYPE);
    }

    public AuthenticationTypeCyclicGenerator(String[] customList) {
        super(customList);
    }
}
