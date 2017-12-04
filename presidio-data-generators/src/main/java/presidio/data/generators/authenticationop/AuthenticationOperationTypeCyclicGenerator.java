package presidio.data.generators.authenticationop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.authentication.AUTHENTICATION_OPERATION_TYPE;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class AuthenticationOperationTypeCyclicGenerator extends CyclicValuesGenerator<OperationType> implements IOperationTypeGenerator {

    private static final OperationType[] DEFAULT_AUTHENTICATION_TYPES = getDefaultAuthenticationOperationTypes();

    private static OperationType[] getDefaultAuthenticationOperationTypes(){
        List<OperationType> ret = new ArrayList();
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_LOG_ON_INTERACTIVELY.value, Arrays.asList(new String[] {""})));
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_LOG_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER.value, Arrays.asList(new String[] {"INTERACTIVE_REMOTE"})));
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_FAILED_TO_AUTHENTICATE_THROUGH_KERBEROS.value, Arrays.asList(new String[] {""})));
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY.value, Arrays.asList(new String[] {""})));
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_LOGGED_ON_INTERACTIVELY_FROM_A_REMOTE_COMPUTER.value, Arrays.asList(new String[] {"INTERACTIVE_REMOTE"})));
        ret.add(new OperationType(AUTHENTICATION_OPERATION_TYPE.USER_AUTHENTICATED_THROUGH_KERBEROS.value, Arrays.asList(new String[] {""})));

        return ret.toArray(new OperationType[ret.size()]);
    }

    public AuthenticationOperationTypeCyclicGenerator() {
        super(DEFAULT_AUTHENTICATION_TYPES);
    }

    public AuthenticationOperationTypeCyclicGenerator(OperationType[] customList) {
        super(customList);
    }
    public AuthenticationOperationTypeCyclicGenerator(OperationType operationType) {
        super(operationType);
    }
}
