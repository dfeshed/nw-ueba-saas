package presidio.data.generators.activedirectoryop;

import presidio.data.domain.event.OperationType;
import presidio.data.generators.common.CyclicValuesGenerator;
import presidio.data.domain.event.activedirectory.AD_OPERATION_TYPE;
import presidio.data.generators.common.IOperationTypeGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class is one element data provider from a cyclic list of string values - ADE File
 */
public class ActiveDirOperationTypeCyclicGenerator extends CyclicValuesGenerator<OperationType> implements IOperationTypeGenerator {
    private static final String[] DEFAULT_AD_OPERATION_TYPE_NAMES = {
            AD_OPERATION_TYPE.OWNER_CHANGED_ON_COMPUTER_OBJECT.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_COMPUTER_OBJECT.value,
            AD_OPERATION_TYPE.COMPUTER_RENAMED.value,
            AD_OPERATION_TYPE.COMPUTER_REMOVED.value,
            AD_OPERATION_TYPE.COMPUTER_MOVED.value,
            AD_OPERATION_TYPE.COMPUTER_ADDED.value,
            AD_OPERATION_TYPE.COMPUTER_ACCOUNT_ENABLED.value,
            AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DISABLED.value,
            AD_OPERATION_TYPE.OWNER_CHANGED_ON_GROUP_OBJECT.value,
            AD_OPERATION_TYPE.NESTED_MEMBER_REMOVED_FROM_GROUP.value,
            AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_GROUP.value,
            AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_GROUP.value,
            AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value,
            AD_OPERATION_TYPE.GROUP_SAMACCOUNTNAME_CHANGED.value,
            AD_OPERATION_TYPE.GROUP_RENAMED.value,
            AD_OPERATION_TYPE.GROUP_OBJECT_REMOVED.value,
            AD_OPERATION_TYPE.GROUP_OBJECT_MOVED.value,
            AD_OPERATION_TYPE.GROUP_OBJECT_ADDED.value,
            AD_OPERATION_TYPE.GROUP_MEMBER_OF_REMOVED.value,
            AD_OPERATION_TYPE.GROUP_MEMBER_OF_ADDED.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_GROUP_OBJECT.value,
            AD_OPERATION_TYPE.WHEN_SESSION_LIMIT_IS_REACHED_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.USER_USERWORKSTATIONS_REMOVED.value,
            AD_OPERATION_TYPE.USER_USERWORKSTATIONS_ADDED.value,
            AD_OPERATION_TYPE.USER_USERPRINCIPALNAME_CHANGED.value,
            AD_OPERATION_TYPE.USER_USE_DES_ENCRYPTION_TYPES_FOR_THIS_ACCOUNT_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_STORE_PASSWORD_USING_REVERSIBLE_ENCRYPTION_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_SMART_CARD_IS_REQUIRED_FOR_INTERACTIVE_LOGON_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_SAMACCOUNTNAME_CHANGED.value,
            AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value,
            AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value,
            AD_OPERATION_TYPE.USER_OBJECT_REMOVED.value,
            AD_OPERATION_TYPE.USER_OBJECT_MOVED.value,
            AD_OPERATION_TYPE.USER_OBJECT_ADDED.value,
            AD_OPERATION_TYPE.USER_MUST_CHANGE_PASSWORD_AT_NEXT_LOGON_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_MEMBER_OF_REMOVED.value,
            AD_OPERATION_TYPE.USER_MEMBER_OF_ADDED.value,
            AD_OPERATION_TYPE.USER_LOGONHOURS_CHANGED.value,
            AD_OPERATION_TYPE.USER_DO_NOT_REQUIRE_KERBEROS_PREAUTHENTICATION_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNTEXPIRES_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_IS_TRUSTED_FOR_DELEGATION_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_IS_SENSITIVE_AND_CANNOT_BE_DELEGATED_OPTION_CHANGED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value,
            AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value,
            AD_OPERATION_TYPE.SERVICE_REMOVED_FROM_DELEGATION_LIST_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.SERVICE_ADDED_TO_DELEGATION_LIST_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.REQUIRE_USERS_PERMISSION_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.PUBLISHED_CERTIFICATE_REMOVED_FROM_USER_OBJECT.value,
            AD_OPERATION_TYPE.PUBLISHED_CERTIFICATE_ADDED_TO_USER_OBJECT.value,
            AD_OPERATION_TYPE.PRIMARY_GROUP_ID_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.OWNER_CHANGED_ON_USER_OBJECT.value,
            AD_OPERATION_TYPE.LOGON_SCRIPT_CHANGED_ON_USER_OBJECT.value,
            AD_OPERATION_TYPE.LEVEL_OF_CONTROL_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.IDLE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.END_A_DISCONNECTED_SESSION_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.ENABLE_REMOTE_CONTROL_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.DOMAIN_USER_RENAMED.value,
            AD_OPERATION_TYPE.DELEGATION_AUTHENTICATION_PROTOCOL_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_USER_OBJECT.value,
            AD_OPERATION_TYPE.ALLOW_RECONNECTION_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.ACTIVE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT.value,
            AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_RENAMED.value,
            AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN.value,
            AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_ADDED_TO_DOMAIN.value,
            AD_OPERATION_TYPE.GUEST_ACCOUNT_ENABLED.value,
            AD_OPERATION_TYPE.GUEST_ACCOUNT_DISABLED.value,
            AD_OPERATION_TYPE.DOMAIN_CONTROLLER_RENAMED.value,
            AD_OPERATION_TYPE.DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN.value,
            AD_OPERATION_TYPE.DOMAIN_CONTROLLER_ADDED_TO_DOMAIN.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_DOMAIN_OBJECT.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_ADMINSDHOLDER_OBJECT.value,
            AD_OPERATION_TYPE.NESTED_MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP.value,
            AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value,
            AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP.value,
            AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value,
            AD_OPERATION_TYPE.NETLOGON_SITENAME_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_SITECOVERAGE_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_LDAPSRVWEIGHT_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_LDAPSRVPRIORITY_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_GCSITECOVERAGE_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_DNSAVOIDREGISTERRECORDS_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_DIAGNOSTIC_LOGGING_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_CLOSESITETIMEOUT_PARAMETER_CHANGED.value,
            AD_OPERATION_TYPE.NETLOGON_AUTOSITECOVERAGE_FLAG_CHANGED.value,
            AD_OPERATION_TYPE.SUBORDINATE_OU_RENAMED.value,
            AD_OPERATION_TYPE.SUBORDINATE_OU_REMOVED.value,
            AD_OPERATION_TYPE.SUBORDINATE_OU_ADDED.value,
            AD_OPERATION_TYPE.OU_GROUP_POLICY_ORDER_CHANGED.value,
            AD_OPERATION_TYPE.DOMAIN_CONTROLLER_REMOVED_FROM_OU.value,
            AD_OPERATION_TYPE.DOMAIN_CONTROLLER_ADDED_TO_OU.value,
            AD_OPERATION_TYPE.DACL_CHANGED_ON_OU_OBJECT.value,
            AD_OPERATION_TYPE.ALTERNATE_UPN_SUFFIX_REMOVED_FROM_OU.value,
            AD_OPERATION_TYPE.ALTERNATE_UPN_SUFFIX_ADDED_TO_OU.value,
            AD_OPERATION_TYPE.SCHEMA_VERSION_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_OBJECT_ENABLED.value,
            AD_OPERATION_TYPE.SCHEMA_OBJECT_DISABLED.value,
            AD_OPERATION_TYPE.SCHEMA_CLASS_DEFAULT_SECURITY_DESCRIPTOR_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_CLASS_ADDED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_RODC_FILTERED_FLAG_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_INDEXING_FLAG_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_GC_FLAG_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_DEFAULTHIDINGVALUE_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_CONFIDENTIAL_FLAG_CHANGED.value,
            AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_ADDED.value,
            AD_OPERATION_TYPE.NEW_CLASS_ADDED_TO_POSSIBLE_SUPERIORS_IN_SCHEMA.value,
            AD_OPERATION_TYPE.NEW_CLASS_ADDED_TO_AUXILIARY_CLASSES_IN_SCHEMA.value,
            AD_OPERATION_TYPE.CLASS_REMOVED_FROM_POSSIBLE_SUPERIORS_IN_SCHEMA.value,
            AD_OPERATION_TYPE.CLASS_REMOVED_FROM_AUXILIARY_CLASSES_IN_SCHEMA.value,
            AD_OPERATION_TYPE.ATTRIBUTE_REMOVED_FROM_OPTIONAL_ATTRIBUTES.value,
            AD_OPERATION_TYPE.ATTRIBUTE_ADDED_TO_OPTIONAL_ATTRIBUTES.value,
            AD_OPERATION_TYPE.SYSVOL_FOLDER_OWNERSHIP_CHANGED.value,
            AD_OPERATION_TYPE.SYSVOL_FOLDER_AUDITING_CHANGED.value,
            AD_OPERATION_TYPE.SYSVOL_FOLDER_ACCESS_RIGHTS_CHANGED.value,
            AD_OPERATION_TYPE.STRONG_AUTHENTICATION_METHOD_CHANGED.value,
            AD_OPERATION_TYPE.STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED.value,
            AD_OPERATION_TYPE.STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED.value,
            AD_OPERATION_TYPE.STRONG_AUTHENTICATION_REQUIREMENT_CHANGED.value,
            AD_OPERATION_TYPE.USER_PASSWORD_RESET.value
    };

    private static final OperationType[] DEFAULT_AD_OPERATION_TYPES = getDefaultAdOperationTypes(Collections.emptyList());
    private static final OperationType[] DEFAULT_ACTIVE_DIRECTORY_OPERATION_TYPES = getDefaultAdOperationTypes();

    private static OperationType[] getDefaultAdOperationTypes(){
        List<OperationType> ret = new ArrayList();
        ret.add(new OperationType(AD_OPERATION_TYPE.OWNER_CHANGED_ON_COMPUTER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_COMPUTER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_RENAMED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_REMOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_MOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_ENABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.COMPUTER_ACCOUNT_DISABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.OWNER_CHANGED_ON_GROUP_OBJECT.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NESTED_MEMBER_REMOVED_FROM_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.MEMBER_ADDED_TO_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_SAMACCOUNTNAME_CHANGED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_RENAMED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_OBJECT_REMOVED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_OBJECT_MOVED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_OBJECT_ADDED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_MEMBER_OF_REMOVED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GROUP_MEMBER_OF_ADDED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_GROUP_OBJECT.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.WHEN_SESSION_LIMIT_IS_REACHED_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_USERWORKSTATIONS_REMOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_USERWORKSTATIONS_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_USERPRINCIPALNAME_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_USE_DES_ENCRYPTION_TYPES_FOR_THIS_ACCOUNT_OPTION_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_STORE_PASSWORD_USING_REVERSIBLE_ENCRYPTION_OPTION_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_SMART_CARD_IS_REQUIRED_FOR_INTERACTIVE_LOGON_OPTION_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_SAMACCOUNTNAME_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED_BY_NON_OWNER.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_PASSWORD_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_OBJECT_REMOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_OBJECT_MOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_OBJECT_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_MUST_CHANGE_PASSWORD_AT_NEXT_LOGON_OPTION_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_MEMBER_OF_REMOVED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_MEMBER_OF_ADDED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_LOGONHOURS_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_DO_NOT_REQUIRE_KERBEROS_PREAUTHENTICATION_OPTION_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNTEXPIRES_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_UNLOCKED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_TYPE_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_RE_ENABLED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_LOCKED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_IS_TRUSTED_FOR_DELEGATION_OPTION_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_IS_SENSITIVE_AND_CANNOT_BE_DELEGATED_OPTION_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_ENABLED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_ACCOUNT_DISABLED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SERVICE_REMOVED_FROM_DELEGATION_LIST_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SERVICE_ADDED_TO_DELEGATION_LIST_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.REQUIRE_USERS_PERMISSION_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.PUBLISHED_CERTIFICATE_REMOVED_FROM_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.PUBLISHED_CERTIFICATE_ADDED_TO_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.PRIMARY_GROUP_ID_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.OWNER_CHANGED_ON_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.LOGON_SCRIPT_CHANGED_ON_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.LEVEL_OF_CONTROL_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.IDLE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.END_A_DISCONNECTED_SESSION_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ENABLE_REMOTE_CONTROL_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_USER_RENAMED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DELEGATION_AUTHENTICATION_PROTOCOL_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ALLOW_RECONNECTION_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ACTIVE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_RENAMED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.READ_ONLY_DOMAIN_CONTROLLER_ADDED_TO_DOMAIN.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GUEST_ACCOUNT_ENABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.GUEST_ACCOUNT_DISABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_CONTROLLER_RENAMED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_CONTROLLER_ADDED_TO_DOMAIN.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_DOMAIN_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_ADMINSDHOLDER_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NESTED_MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","SECURITY_SENSITIVE_OPERATION","GROUP_MEMBERSHIP_REMOVE_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","GROUP_MEMBERSHIP_OPERATION","GROUP_MEMBERSHIP_ADD_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_SITENAME_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_SITECOVERAGE_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_LDAPSRVWEIGHT_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_LDAPSRVPRIORITY_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_GCSITECOVERAGE_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_DNSAVOIDREGISTERRECORDS_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_DIAGNOSTIC_LOGGING_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_CLOSESITETIMEOUT_PARAMETER_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NETLOGON_AUTOSITECOVERAGE_FLAG_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SUBORDINATE_OU_RENAMED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SUBORDINATE_OU_REMOVED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SUBORDINATE_OU_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.OU_GROUP_POLICY_ORDER_CHANGED.value, Arrays.asList(new String[] {"GROUP_MEMBERSHIP_OPERATION"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_CONTROLLER_REMOVED_FROM_OU.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DOMAIN_CONTROLLER_ADDED_TO_OU.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.DACL_CHANGED_ON_OU_OBJECT.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ALTERNATE_UPN_SUFFIX_REMOVED_FROM_OU.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ALTERNATE_UPN_SUFFIX_ADDED_TO_OU.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_VERSION_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_OBJECT_ENABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_OBJECT_DISABLED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_CLASS_DEFAULT_SECURITY_DESCRIPTOR_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_CLASS_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_RODC_FILTERED_FLAG_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_INDEXING_FLAG_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_GC_FLAG_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_DEFAULTHIDINGVALUE_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_CONFIDENTIAL_FLAG_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SCHEMA_ATTRIBUTE_ADDED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NEW_CLASS_ADDED_TO_POSSIBLE_SUPERIORS_IN_SCHEMA.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.NEW_CLASS_ADDED_TO_AUXILIARY_CLASSES_IN_SCHEMA.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.CLASS_REMOVED_FROM_POSSIBLE_SUPERIORS_IN_SCHEMA.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.CLASS_REMOVED_FROM_AUXILIARY_CLASSES_IN_SCHEMA.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ATTRIBUTE_REMOVED_FROM_OPTIONAL_ATTRIBUTES.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.ATTRIBUTE_ADDED_TO_OPTIONAL_ATTRIBUTES.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SYSVOL_FOLDER_OWNERSHIP_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SYSVOL_FOLDER_AUDITING_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.SYSVOL_FOLDER_ACCESS_RIGHTS_CHANGED.value, Arrays.asList(new String[] {"OBJECT_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.STRONG_AUTHENTICATION_METHOD_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.STRONG_AUTHENTICATION_REQUIREMENT_CHANGED.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));
        ret.add(new OperationType(AD_OPERATION_TYPE.USER_PASSWORD_RESET.value, Arrays.asList(new String[] {"SECURITY_SENSITIVE_OPERATION","USER_MANAGEMENT"})));

        return ret.toArray(new OperationType[ret.size()]);
    }

    private static OperationType[] getDefaultAdOperationTypes(List<String> categories){
        List<OperationType> ret = Arrays.stream(DEFAULT_AD_OPERATION_TYPE_NAMES).map(s -> new OperationType(s, categories)).collect(Collectors.toList());
        return ret.toArray(new OperationType[ret.size()]);
    }
    public ActiveDirOperationTypeCyclicGenerator() {
        super(DEFAULT_ACTIVE_DIRECTORY_OPERATION_TYPES);
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