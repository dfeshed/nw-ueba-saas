package presidio.data.domain.event.activedirectory;

import java.util.Arrays;

public enum AD_OPERATION_TYPE {
    PERMISSIONS_ON_OBJECT_CHANGED("PERMISSIONS_ON_OBJECT_CHANGED"),
    SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT("SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT"),
    USER_ACCOUNT_CREATED("USER_ACCOUNT_CREATED"),
    USER_ACCOUNT_ENABLED("USER_ACCOUNT_ENABLED"),
    USER_PASSWORD_CHANGED("USER_PASSWORD_CHANGED"),
    USER_PASSWORD_RESET("USER_PASSWORD_RESET"),
    USER_ACCOUNT_DISABLED("USER_ACCOUNT_DISABLED"),
    USER_ACCOUNT_DELETED("USER_ACCOUNT_DELETED"),
    SECURITY_ENABLED_GLOBAL_GROUP_CREATED("SECURITY_ENABLED_GLOBAL_GROUP_CREATED"),
    MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP("MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP"),
    MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP("MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP"),
    SECURITY_ENABLED_GLOBAL_GROUP_CHANGED("SECURITY_ENABLED_GLOBAL_GROUP_CHANGED"),
    SECURITY_ENABLED_GLOBAL_GROUP_DELETED("SECURITY_ENABLED_GLOBAL_GROUP_DELETED"),
    SECURITY_ENABLED_LOCAL_GROUP_CHANGED("SECURITY_ENABLED_LOCAL_GROUP_CHANGED"),
    SECURITY_ENABLED_LOCAL_GROUP_CREATED("SECURITY_ENABLED_LOCAL_GROUP_CREATED"),
    MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP("MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP"),
    MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP("MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP"),
    SECURITY_ENABLED_LOCAL_GROUP_DELETED("SECURITY_ENABLED_LOCAL_GROUP_DELETED"),
    USER_ACCOUNT_CHANGED("USER_ACCOUNT_CHANGED"),
    DOMAIN_POLICY_CHANGED("DOMAIN_POLICY_CHANGED"),
    USER_ACCOUNT_LOCKED("USER_ACCOUNT_LOCKED"),
    COMPUTER_ACCOUNT_CREATED("COMPUTER_ACCOUNT_CREATED"),
    COMPUTER_ACCOUNT_CHANGED("COMPUTER_ACCOUNT_CHANGED"),
    COMPUTER_ACCOUNT_DELETED("COMPUTER_ACCOUNT_DELETED"),
    SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED("SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED"),
    MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP("MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP"),
    MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP("MEMBER_REMOVED_FROM_SECURITY_ENABLED_UNIVERSAL_GROUP"),
    SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED("SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED"),
    SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED("SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED"),
    GROUP_TYPE_CHANGED("GROUP_TYPE_CHANGED"),
    USER_ACCOUNT_UNLOCKED("USER_ACCOUNT_UNLOCKED"),
    ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD("ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD"),
    DIRECTORY_SERVICE_OBJECT_MODIFIED("DIRECTORY_SERVICE_OBJECT_MODIFIED"),
    CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP("CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP"),
    CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP("CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP"),
    MEMBER_REMOVED_FROM_GROUP("MEMBER_REMOVED_FROM_GROUP"),
    /**
     * ==================================================================
     * TODO: The following are old operation types related to Quest data.
     * TODO: These operation types are deprecated and should be removed.
     * ==================================================================
     */
    OWNER_CHANGED_ON_COMPUTER_OBJECT("OWNER_CHANGED_ON_COMPUTER_OBJECT"),
    DACL_CHANGED_ON_COMPUTER_OBJECT("DACL_CHANGED_ON_COMPUTER_OBJECT"),
    COMPUTER_RENAMED("COMPUTER_RENAMED"),
    COMPUTER_REMOVED("COMPUTER_REMOVED"),
    COMPUTER_MOVED("COMPUTER_MOVED"),
    COMPUTER_ADDED("COMPUTER_ADDED"),
    COMPUTER_ACCOUNT_ENABLED("COMPUTER_ACCOUNT_ENABLED"),
    COMPUTER_ACCOUNT_DISABLED("COMPUTER_ACCOUNT_DISABLED"),
    OWNER_CHANGED_ON_GROUP_OBJECT("OWNER_CHANGED_ON_GROUP_OBJECT"),
    NESTED_MEMBER_REMOVED_FROM_GROUP("NESTED_MEMBER_REMOVED_FROM_GROUP"),
    NESTED_MEMBER_ADDED_TO_GROUP("NESTED_MEMBER_ADDED_TO_GROUP"),
    MEMBER_ADDED_TO_GROUP("MEMBER_ADDED_TO_GROUP"),
    GROUP_SAMACCOUNTNAME_CHANGED("GROUP_SAMACCOUNTNAME_CHANGED"),
    GROUP_RENAMED("GROUP_RENAMED"),
    GROUP_OBJECT_REMOVED("GROUP_OBJECT_REMOVED"),
    GROUP_OBJECT_MOVED("GROUP_OBJECT_MOVED"),
    GROUP_OBJECT_ADDED("GROUP_OBJECT_ADDED"),
    GROUP_MEMBER_OF_REMOVED("GROUP_MEMBER_OF_REMOVED"),
    GROUP_MEMBER_OF_ADDED("GROUP_MEMBER_OF_ADDED"),
    DACL_CHANGED_ON_GROUP_OBJECT("DACL_CHANGED_ON_GROUP_OBJECT"),
    WHEN_SESSION_LIMIT_IS_REACHED_CHANGED_FOR_USER_OBJECT("WHEN_SESSION_LIMIT_IS_REACHED_CHANGED_FOR_USER_OBJECT"),
    USER_USERWORKSTATIONS_REMOVED("USER_USERWORKSTATIONS_REMOVED"),
    USER_USERWORKSTATIONS_ADDED("USER_USERWORKSTATIONS_ADDED"),
    USER_USERPRINCIPALNAME_CHANGED("USER_USERPRINCIPALNAME_CHANGED"),
    USER_USE_DES_ENCRYPTION_TYPES_FOR_THIS_ACCOUNT_OPTION_CHANGED("USER_USE_DES_ENCRYPTION_TYPES_FOR_THIS_ACCOUNT_OPTION_CHANGED"),
    USER_STORE_PASSWORD_USING_REVERSIBLE_ENCRYPTION_OPTION_CHANGED("USER_STORE_PASSWORD_USING_REVERSIBLE_ENCRYPTION_OPTION_CHANGED"),
    USER_SMART_CARD_IS_REQUIRED_FOR_INTERACTIVE_LOGON_OPTION_CHANGED("USER_SMART_CARD_IS_REQUIRED_FOR_INTERACTIVE_LOGON_OPTION_CHANGED"),
    USER_SAMACCOUNTNAME_CHANGED("USER_SAMACCOUNTNAME_CHANGED"),
    USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED("USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED"),
    USER_PASSWORD_CHANGED_BY_NON_OWNER("USER_PASSWORD_CHANGED_BY_NON_OWNER"),
    USER_OBJECT_REMOVED("USER_OBJECT_REMOVED"),
    USER_OBJECT_MOVED("USER_OBJECT_MOVED"),
    USER_OBJECT_ADDED("USER_OBJECT_ADDED"),
    USER_MUST_CHANGE_PASSWORD_AT_NEXT_LOGON_OPTION_CHANGED("USER_MUST_CHANGE_PASSWORD_AT_NEXT_LOGON_OPTION_CHANGED"),
    USER_MEMBER_OF_REMOVED("USER_MEMBER_OF_REMOVED"),
    USER_MEMBER_OF_ADDED("USER_MEMBER_OF_ADDED"),
    USER_LOGONHOURS_CHANGED("USER_LOGONHOURS_CHANGED"),
    USER_DO_NOT_REQUIRE_KERBEROS_PREAUTHENTICATION_OPTION_CHANGED("USER_DO_NOT_REQUIRE_KERBEROS_PREAUTHENTICATION_OPTION_CHANGED"),
    USER_ACCOUNTEXPIRES_CHANGED("USER_ACCOUNTEXPIRES_CHANGED"),
    USER_ACCOUNT_TYPE_CHANGED("USER_ACCOUNT_TYPE_CHANGED"),
    USER_ACCOUNT_RE_ENABLED("USER_ACCOUNT_RE_ENABLED"),
    USER_ACCOUNT_IS_TRUSTED_FOR_DELEGATION_OPTION_CHANGED("USER_ACCOUNT_IS_TRUSTED_FOR_DELEGATION_OPTION_CHANGED"),
    USER_ACCOUNT_IS_SENSITIVE_AND_CANNOT_BE_DELEGATED_OPTION_CHANGED("USER_ACCOUNT_IS_SENSITIVE_AND_CANNOT_BE_DELEGATED_OPTION_CHANGED"),
    SERVICE_REMOVED_FROM_DELEGATION_LIST_FOR_USER_OBJECT("SERVICE_REMOVED_FROM_DELEGATION_LIST_FOR_USER_OBJECT"),
    SERVICE_ADDED_TO_DELEGATION_LIST_FOR_USER_OBJECT("SERVICE_ADDED_TO_DELEGATION_LIST_FOR_USER_OBJECT"),
    REQUIRE_USERS_PERMISSION_CHANGED_FOR_USER_OBJECT("REQUIRE_USERS_PERMISSION_CHANGED_FOR_USER_OBJECT"),
    PUBLISHED_CERTIFICATE_REMOVED_FROM_USER_OBJECT("PUBLISHED_CERTIFICATE_REMOVED_FROM_USER_OBJECT"),
    PUBLISHED_CERTIFICATE_ADDED_TO_USER_OBJECT("PUBLISHED_CERTIFICATE_ADDED_TO_USER_OBJECT"),
    PRIMARY_GROUP_ID_CHANGED_FOR_USER_OBJECT("PRIMARY_GROUP_ID_CHANGED_FOR_USER_OBJECT"),
    OWNER_CHANGED_ON_USER_OBJECT("OWNER_CHANGED_ON_USER_OBJECT"),
    LOGON_SCRIPT_CHANGED_ON_USER_OBJECT("LOGON_SCRIPT_CHANGED_ON_USER_OBJECT"),
    LEVEL_OF_CONTROL_CHANGED_FOR_USER_OBJECT("LEVEL_OF_CONTROL_CHANGED_FOR_USER_OBJECT"),
    IDLE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT("IDLE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT"),
    END_A_DISCONNECTED_SESSION_CHANGED_FOR_USER_OBJECT("END_A_DISCONNECTED_SESSION_CHANGED_FOR_USER_OBJECT"),
    ENABLE_REMOTE_CONTROL_CHANGED_FOR_USER_OBJECT("ENABLE_REMOTE_CONTROL_CHANGED_FOR_USER_OBJECT"),
    DOMAIN_USER_RENAMED("DOMAIN_USER_RENAMED"),
    DELEGATION_AUTHENTICATION_PROTOCOL_CHANGED_FOR_USER_OBJECT("DELEGATION_AUTHENTICATION_PROTOCOL_CHANGED_FOR_USER_OBJECT"),
    DACL_CHANGED_ON_USER_OBJECT("DACL_CHANGED_ON_USER_OBJECT"),
    ALLOW_RECONNECTION_CHANGED_FOR_USER_OBJECT("ALLOW_RECONNECTION_CHANGED_FOR_USER_OBJECT"),
    ACTIVE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT("ACTIVE_SESSION_LIMIT_CHANGED_FOR_USER_OBJECT"),
    READ_ONLY_DOMAIN_CONTROLLER_RENAMED("READ_ONLY_DOMAIN_CONTROLLER_RENAMED"),
    READ_ONLY_DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN("READ_ONLY_DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN"),
    READ_ONLY_DOMAIN_CONTROLLER_ADDED_TO_DOMAIN("READ_ONLY_DOMAIN_CONTROLLER_ADDED_TO_DOMAIN"),
    GUEST_ACCOUNT_ENABLED("GUEST_ACCOUNT_ENABLED"),
    GUEST_ACCOUNT_DISABLED("GUEST_ACCOUNT_DISABLED"),
    DOMAIN_CONTROLLER_RENAMED("DOMAIN_CONTROLLER_RENAMED"),
    DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN("DOMAIN_CONTROLLER_REMOVED_FROM_DOMAIN"),
    DOMAIN_CONTROLLER_ADDED_TO_DOMAIN("DOMAIN_CONTROLLER_ADDED_TO_DOMAIN"),
    DACL_CHANGED_ON_DOMAIN_OBJECT("DACL_CHANGED_ON_DOMAIN_OBJECT"),
    DACL_CHANGED_ON_ADMINSDHOLDER_OBJECT("DACL_CHANGED_ON_ADMINSDHOLDER_OBJECT"),
    NESTED_MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP("NESTED_MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP"),
    NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP("NESTED_MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP"),
    MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP("MEMBER_REMOVED_FROM_CRITICAL_ENTERPRISE_GROUP"),
    MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP("MEMBER_ADDED_TO_CRITICAL_ENTERPRISE_GROUP"),
    NETLOGON_SITENAME_PARAMETER_CHANGED("NETLOGON_SITENAME_PARAMETER_CHANGED"),
    NETLOGON_SITECOVERAGE_PARAMETER_CHANGED("NETLOGON_SITECOVERAGE_PARAMETER_CHANGED"),
    NETLOGON_LDAPSRVWEIGHT_PARAMETER_CHANGED("NETLOGON_LDAPSRVWEIGHT_PARAMETER_CHANGED"),
    NETLOGON_LDAPSRVPRIORITY_PARAMETER_CHANGED("NETLOGON_LDAPSRVPRIORITY_PARAMETER_CHANGED"),
    NETLOGON_GCSITECOVERAGE_PARAMETER_CHANGED("NETLOGON_GCSITECOVERAGE_PARAMETER_CHANGED"),
    NETLOGON_DNSAVOIDREGISTERRECORDS_PARAMETER_CHANGED("NETLOGON_DNSAVOIDREGISTERRECORDS_PARAMETER_CHANGED"),
    NETLOGON_DIAGNOSTIC_LOGGING_PARAMETER_CHANGED("NETLOGON_DIAGNOSTIC_LOGGING_PARAMETER_CHANGED"),
    NETLOGON_CLOSESITETIMEOUT_PARAMETER_CHANGED("NETLOGON_CLOSESITETIMEOUT_PARAMETER_CHANGED"),
    NETLOGON_AUTOSITECOVERAGE_FLAG_CHANGED("NETLOGON_AUTOSITECOVERAGE_FLAG_CHANGED"),
    SUBORDINATE_OU_RENAMED("SUBORDINATE_OU_RENAMED"),
    SUBORDINATE_OU_REMOVED("SUBORDINATE_OU_REMOVED"),
    SUBORDINATE_OU_ADDED("SUBORDINATE_OU_ADDED"),
    OU_GROUP_POLICY_ORDER_CHANGED("OU_GROUP_POLICY_ORDER_CHANGED"),
    DOMAIN_CONTROLLER_REMOVED_FROM_OU("DOMAIN_CONTROLLER_REMOVED_FROM_OU"),
    DOMAIN_CONTROLLER_ADDED_TO_OU("DOMAIN_CONTROLLER_ADDED_TO_OU"),
    DACL_CHANGED_ON_OU_OBJECT("DACL_CHANGED_ON_OU_OBJECT"),
    ALTERNATE_UPN_SUFFIX_REMOVED_FROM_OU("ALTERNATE_UPN_SUFFIX_REMOVED_FROM_OU"),
    ALTERNATE_UPN_SUFFIX_ADDED_TO_OU("ALTERNATE_UPN_SUFFIX_ADDED_TO_OU"),
    SCHEMA_VERSION_CHANGED("SCHEMA_VERSION_CHANGED"),
    SCHEMA_OBJECT_ENABLED("SCHEMA_OBJECT_ENABLED"),
    SCHEMA_OBJECT_DISABLED("SCHEMA_OBJECT_DISABLED"),
    SCHEMA_CLASS_DEFAULT_SECURITY_DESCRIPTOR_CHANGED("SCHEMA_CLASS_DEFAULT_SECURITY_DESCRIPTOR_CHANGED"),
    SCHEMA_CLASS_ADDED("SCHEMA_CLASS_ADDED"),
    SCHEMA_ATTRIBUTE_RODC_FILTERED_FLAG_CHANGED("SCHEMA_ATTRIBUTE_RODC_FILTERED_FLAG_CHANGED"),
    SCHEMA_ATTRIBUTE_INDEXING_FLAG_CHANGED("SCHEMA_ATTRIBUTE_INDEXING_FLAG_CHANGED"),
    SCHEMA_ATTRIBUTE_GC_FLAG_CHANGED("SCHEMA_ATTRIBUTE_GC_FLAG_CHANGED"),
    SCHEMA_ATTRIBUTE_DEFAULTHIDINGVALUE_CHANGED("SCHEMA_ATTRIBUTE_DEFAULTHIDINGVALUE_CHANGED"),
    SCHEMA_ATTRIBUTE_CONFIDENTIAL_FLAG_CHANGED("SCHEMA_ATTRIBUTE_CONFIDENTIAL_FLAG_CHANGED"),
    SCHEMA_ATTRIBUTE_ADDED("SCHEMA_ATTRIBUTE_ADDED"),
    NEW_CLASS_ADDED_TO_POSSIBLE_SUPERIORS_IN_SCHEMA("NEW_CLASS_ADDED_TO_POSSIBLE_SUPERIORS_IN_SCHEMA"),
    NEW_CLASS_ADDED_TO_AUXILIARY_CLASSES_IN_SCHEMA("NEW_CLASS_ADDED_TO_AUXILIARY_CLASSES_IN_SCHEMA"),
    CLASS_REMOVED_FROM_POSSIBLE_SUPERIORS_IN_SCHEMA("CLASS_REMOVED_FROM_POSSIBLE_SUPERIORS_IN_SCHEMA"),
    CLASS_REMOVED_FROM_AUXILIARY_CLASSES_IN_SCHEMA("CLASS_REMOVED_FROM_AUXILIARY_CLASSES_IN_SCHEMA"),
    ATTRIBUTE_REMOVED_FROM_OPTIONAL_ATTRIBUTES("ATTRIBUTE_REMOVED_FROM_OPTIONAL_ATTRIBUTES"),
    ATTRIBUTE_ADDED_TO_OPTIONAL_ATTRIBUTES("ATTRIBUTE_ADDED_TO_OPTIONAL_ATTRIBUTES"),
    SYSVOL_FOLDER_OWNERSHIP_CHANGED("SYSVOL_FOLDER_OWNERSHIP_CHANGED"),
    SYSVOL_FOLDER_AUDITING_CHANGED("SYSVOL_FOLDER_AUDITING_CHANGED"),
    SYSVOL_FOLDER_ACCESS_RIGHTS_CHANGED("SYSVOL_FOLDER_ACCESS_RIGHTS_CHANGED"),
    STRONG_AUTHENTICATION_METHOD_CHANGED("STRONG_AUTHENTICATION_METHOD_CHANGED"),
    STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED("STRONG_AUTHENTICATION_PHONE_APP_DETAIL_CHANGED"),
    STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED("STRONG_AUTHENTICATION_PHONE_USER_DETAIL_CHANGED"),
    STRONG_AUTHENTICATION_REQUIREMENT_CHANGED("STRONG_AUTHENTICATION_REQUIREMENT_CHANGED");

    public final String value;

    AD_OPERATION_TYPE(String value) {
        this.value = value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}