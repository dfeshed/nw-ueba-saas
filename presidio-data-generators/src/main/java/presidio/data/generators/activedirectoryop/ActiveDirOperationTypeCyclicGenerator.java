package presidio.data.generators.activedirectoryop;

import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.common.IStringGenerator;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class ActiveDirOperationTypeCyclicGenerator extends CyclicValuesGenerator<String> implements IStringGenerator
{

    private static final String[] DEFAULT_AD_OPERATION_TYPE = {
            AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value,
            AD_OPERATION_TYPE.PASSWORD_CHANGED.value,
            AD_OPERATION_TYPE.PASSWORD_CHANGED_BY_NON_OWNER.value,
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

    public ActiveDirOperationTypeCyclicGenerator() {
        super(DEFAULT_AD_OPERATION_TYPE);
    }

    public ActiveDirOperationTypeCyclicGenerator(String[] customList) {
        super(customList);
    }

    public ActiveDirOperationTypeCyclicGenerator(String customType) {
        super(customType);
    }
}
