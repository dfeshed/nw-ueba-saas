package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.OperationType;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class ActiveDirOperationTypeCyclicGenerator extends CyclicValuesGenerator<OperationType> implements IOperationTypeGenerator {
    private static final String[] DEFAULT_AD_OPERATION_TYPE_NAMES = {
            AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value,
            AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value,
            AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value,
            AD_OPERATION_TYPE.GROUP_MEMBERSHIP.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value,
            AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value,
            AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value
    };
    private static final OperationType[] DEFAULT_AD_OPERATION_TYPES = getDefaultAdOperationTypes(Collections.emptyList());

    private static OperationType[] getDefaultAdOperationTypes(List<String> categories){
        List<OperationType> ret = Arrays.stream(DEFAULT_AD_OPERATION_TYPE_NAMES).map(s -> new OperationType(s, categories)).collect(Collectors.toList());
        return ret.toArray(new OperationType[ret.size()]);
    }
    public ActiveDirOperationTypeCyclicGenerator() {
        super(DEFAULT_AD_OPERATION_TYPES);
    }

    public ActiveDirOperationTypeCyclicGenerator(List<String> categories) {
        super(getDefaultAdOperationTypes(categories));
    }

    public ActiveDirOperationTypeCyclicGenerator(OperationType[] customList) {
        super(customList);
    }

    public ActiveDirOperationTypeCyclicGenerator(OperationType customType) {
        super(customType);
    }
}
