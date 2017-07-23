package presidio.data.generators.authenticationop;

import presidio.data.generators.common.AbstractCyclicValuesGenerator;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.domain.event.authentication.AUTHENTICATION_TYPE;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class AuthenticationTypeCyclicGenerator extends AbstractCyclicValuesGenerator {

    private static final String[] DEFAULT_AUTHENTICATION_TYPE = {
            AUTHENTICATION_TYPE.AUTHENTICATION_TYPE_TBD.value
    };

    public AuthenticationTypeCyclicGenerator() {
        super(DEFAULT_AUTHENTICATION_TYPE);
    }

    public AuthenticationTypeCyclicGenerator(String[] customList) {
        super(customList);
    }
}
