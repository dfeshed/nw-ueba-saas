package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.OperationType;
import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.common.FixedOperationTypeGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ActiveDirectoryOpGeneratorTemplateFactory {
    private HashMap<String,List<String>> opType2OpCategoryMap = new HashMap<>();

    public ActiveDirectoryOpGeneratorTemplateFactory() {

        // TODO: assign right categories for each operation type
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value,
                Collections.singletonList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value));

        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.GROUP_MEMBERSHIP.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, Collections.emptyList());
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value, Collections.emptyList());


        opType2OpCategoryMap.put(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value,
                Arrays.asList(ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value,
                ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP.value,
                ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD.value ));

        opType2OpCategoryMap.put(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, Collections.emptyList());
    }

    /**
     * Default. Can be instantiated directly, without using this factory class.
     */
    public IActiveDirectoryOperationGenerator getDefaultActiveDirectoryOperationsGenerator() throws GeneratorException {
        ActiveDirectoryOperationGenerator generator = new ActiveDirectoryOperationGenerator();
        return generator;
    }

    /**
     * Operation type -
     * Operation Type Categories list -
     **/
    public IActiveDirectoryOperationGenerator getActiveDirectoryOperationsGenerator(String operationType) throws GeneratorException {
        return getActiveDirectoryOperationsGenerator(operationType,opType2OpCategoryMap.get(operationType));
    }

    /**
     * Operation type -
     * Operation Type Categories list -
     **/
    public IActiveDirectoryOperationGenerator getActiveDirectoryOperationsGenerator(String operationType, List<String> categories) throws GeneratorException {

        ActiveDirectoryOperationGenerator generator = new ActiveDirectoryOperationGenerator();
        IOperationTypeGenerator opTypeGenerator = new FixedOperationTypeGenerator(new OperationType(operationType, opType2OpCategoryMap.get(operationType)));
        generator.setOperationTypeGenerator(opTypeGenerator);

        return generator;
    }


}