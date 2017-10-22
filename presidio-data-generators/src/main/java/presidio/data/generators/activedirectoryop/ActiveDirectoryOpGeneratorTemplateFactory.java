package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.activedirectory.ACTIVEDIRECTORY_OP_TYPE_CATEGORIES;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.common.GeneratorException;

import java.util.HashMap;

public class ActiveDirectoryOpGeneratorTemplateFactory {
    private HashMap<String,String[]> opType2OpCategoryMap = new HashMap<>();

    public ActiveDirectoryOpGeneratorTemplateFactory() {

        String [] categories = new String[] {};

        // TODO: assign right categories for each operation type
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.ACCOUNT_MANAGEMENT.value,
                new String[] {ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value});

        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.GROUP_MEMBERSHIP.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, categories);
        opType2OpCategoryMap.put(AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value, categories);


        opType2OpCategoryMap.put(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value,
                new String[] {ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.SECURITY_SENSITIVE_OPERATION.value,
                ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP.value,
                ACTIVEDIRECTORY_OP_TYPE_CATEGORIES.GROUP_MEMBERSHIP_ADD.value });

        opType2OpCategoryMap.put(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, categories);
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

        ActiveDirectoryOperationGenerator generator = new ActiveDirectoryOperationGenerator();
        ActiveDirOperationTypeCyclicGenerator opTypeGenerator = new ActiveDirOperationTypeCyclicGenerator(new String[] {operationType});
        ActiveDirectoryOpTypeCategoriesGenerator opTypeCategoriesGenerator = new ActiveDirectoryOpTypeCategoriesGenerator(opType2OpCategoryMap.get(operationType));
        generator.setOperationTypeGenerator(opTypeGenerator);
        generator.setOperationTypeCategoriesGenerator(opTypeCategoriesGenerator);

        return generator;
    }
}