package com.rsa.netwitness.presidio.automation.static_content;

import java.util.HashMap;

@Deprecated
public class IndicatorNameToFeatureNameDictionary {

    private static HashMap<String, String> dictionary;

    // File indicators
    private static String highestStartInstantScoreUserIdFileHourly = "highestStartInstantScoreUserIdFileHourly";
    private static String sumOfHighestOperationTypeScoresUserIdFilePermissionChangeFileHourly = "sumOfHighestOperationTypeScoresUserIdFilePermissionChangeFileHourly";
    private static String sumOfHighestOperationTypeScoresUserIdFileActionFileHourly = "sumOfHighestOperationTypeScoresUserIdFileActionFileHourly";
    private static String numberOfSuccessfulFilePermissionChangesUserIdFileHourly = "numberOfSuccessfulFilePermissionChangesUserIdFileHourly";
    private static String numberOfSuccessfulFileActionsUserIdFileHourly = "numberOfSuccessfulFileActionsUserIdFileHourly";
    private static String numberOfFailedFilePermissionChangesUserIdFileHourly = "numberOfFailedFilePermissionChangesUserIdFileHourly";
    private static String numberOfFailedFileActionsUserIdFileHourly = "numberOfFailedFileActionsUserIdFileHourly";
    private static String numberOfDistinctFileOpenedUserIdFileHourly = "numberOfDistinctFileOpenedUserIdFileHourly";
    private static String numberOfDistinctFolderOpenedUserIdFileHourly = "numberOfDistinctFolderOpenedUserIdFileHourly";
    private static String numberOfFileDeletedUserIdFileHourly = "numberOfFileDeletedUserIdFileHourly";
    private static String numberOfSuccessfulFileRenamedUserIdFileHourly = "numberOfSuccessfulFileRenamedUserIdFileHourly";
    private static String numberOfFileMovedUserIdFileHourly = "numberOfFileMovedUserIdFileHourly";
    private static String numberOfFileMovedFromSharedDriveUserIdFileHourly = "numberOfFileMovedFromSharedDriveUserIdFileHourly";
    private static String numberOfFileMovedToSharedDriveUserIdFileHourly = "numberOfFileMovedToSharedDriveUserIdFileHourly";

    // Active Directory indicators
    private static String highestStartInstantScoreUserIdActiveDirectoryHourly = "highestStartInstantScoreUserIdActiveDirectoryHourly";
    private static String sumOfHighestOperationTypeScoresUserIdGroupMembershipSecuritySensitiveActiveDirectoryHourly = "sumOfHighestOperationTypeScoresUserIdGroupMembershipSecuritySensitiveActiveDirectoryHourly";
    private static String numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly = "numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly";
    private static String numberOfGroupMembershipOperationUserIdActiveDirectoryHourly = "numberOfGroupMembershipOperationUserIdActiveDirectoryHourly";
    private static String numberOfSuccessfulObjectManagementOperationUserIdActiveDirectoryHourly = "numberOfSuccessfulObjectManagementOperationUserIdActiveDirectoryHourly";
    private static String numberOfSuccessfulUserManagementSecuritySensitiveOperationUserIdActiveDirectoryHourly = "numberOfSuccessfulUserManagementSecuritySensitiveOperationUserIdActiveDirectoryHourly";
    private static String numberOfFailedOperationTypeUserIdActiveDirectoryHourly = "numberOfFailedOperationTypeUserIdActiveDirectoryHourly";
    private static String adminPasswordChangedScoreUserIdActiveDirectoryHourly = "adminPasswordChangedScoreUserIdActiveDirectoryHourly";
    private static String nonAdminPasswordChangedScoreUserIdActiveDirectoryHourly = "nonAdminPasswordChangedScoreUserIdActiveDirectoryHourly";
    private static String userAccountEnabledScoreUserIdActiveDirectoryHourly = "userAccountEnabledScoreUserIdActiveDirectoryHourly";
    private static String userAccountDisabledScoreUserIdActiveDirectoryHourly = "userAccountDisabledScoreUserIdActiveDirectoryHourly";
    private static String userAccountUnlockedScoreUserIdActiveDirectoryHourly = "userAccountUnlockedScoreUserIdActiveDirectoryHourly";
    private static String userAccountTypeChangedScoreUserIdActiveDirectoryHourly = "userAccountTypeChangedScoreUserIdActiveDirectoryHourly";
    private static String userAccountLockedScoreUserIdActiveDirectoryHourly = "userAccountLockedScoreUserIdActiveDirectoryHourly";
    private static String userPasswordNeverExpiresOptionChangedScoreUserIdActiveDirectoryHourly = "userPasswordNeverExpiresOptionChangedScoreUserIdActiveDirectoryHourly";
    private static String passwordChangedByNonOwnerScoreUserIdActiveDirectoryHourly = "passwordChangedByNonOwnerScoreUserIdActiveDirectoryHourly";
//    private static String nestedMemberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly = "nestedMemberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly";
    private static String memberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly = "memberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly";
    private static String sumOfHighestOperationTypeScoresUserIdObjectManagementActiveDirectoryHourly = "sumOfHighestOperationTypeScoresUserIdObjectManagementActiveDirectoryHourly";

    // Authentication indicators
    private static String highestStartInstantScoreUserIdAuthenticationHourly = "highestStartInstantScoreUserIdAuthenticationHourly";
    private static String sumOfHighestSrcMachineNameRegexClusterScoresUserIdAuthenticationHourly = "sumOfHighestSrcMachineNameRegexClusterScoresUserIdAuthenticationHourly";
    private static String sumOfHighestDstMachineNameRegexClusterScoresUserIdInteractiveRemoteAuthenticationHourly = "sumOfHighestDstMachineNameRegexClusterScoresUserIdInteractiveRemoteAuthenticationHourly";
    private static String numberOfSuccessfulAuthenticationsUserIdAuthenticationHourly = "numberOfSuccessfulAuthenticationsUserIdAuthenticationHourly";
    private static String numberOfFailedAuthenticationsUserIdAuthenticationHourly = "numberOfFailedAuthenticationsUserIdAuthenticationHourly";
    private static String numberOfDistinctDstMachineDomainUserIdAuthenticationHourly = "numberOfDistinctDstMachineDomainUserIdAuthenticationHourly";
    private static String numberOfDistinctSrcMachineIdUserIdAuthenticationHourly = "numberOfDistinctSrcMachineIdUserIdAuthenticationHourly";
    private static String numberOfDistinctDstMachineIdUserIdAuthenticationHourly = "numberOfDistinctDstMachineIdUserIdAuthenticationHourly";
    private static String numberOfDistinctSrcMachineNameRegexClusterUserIdAuthenticationHourly = "numberOfDistinctSrcMachineNameRegexClusterUserIdAuthenticationHourly";
    private static String numberOfDistinctDstMachineNameRegexClusterUserIdAuthenticationHourly = "numberOfDistinctDstMachineNameRegexClusterUserIdAuthenticationHourly";
    private static String numberOfDistinctSiteUserIdAuthenticationHourly = "numberOfDistinctSiteUserIdAuthenticationHourly";
    private static String sumOfHighestSiteScoresUserIdAuthenticationHourly = "sumOfHighestSiteScoresUserIdAuthenticationHourly";

    static {
        dictionary = new HashMap<String, String>();
        dictionary.put("abnormal_event_day_time", highestStartInstantScoreUserIdFileHourly);
        dictionary.put("abnormal_file_permision_change_operation_type", sumOfHighestOperationTypeScoresUserIdFilePermissionChangeFileHourly);
        dictionary.put("abnormal_file_action_operation_type", sumOfHighestOperationTypeScoresUserIdFileActionFileHourly);
        dictionary.put("high_number_of_successful_file_permission_change", numberOfSuccessfulFilePermissionChangesUserIdFileHourly);
        dictionary.put("high_number_of_successful_file_action_operations", numberOfSuccessfulFileActionsUserIdFileHourly);
        dictionary.put("high_number_of_failed_file_permission_change_attempts", numberOfFailedFilePermissionChangesUserIdFileHourly);
        dictionary.put("high_number_of_failed_file_action_attempts", numberOfFailedFileActionsUserIdFileHourly);
        dictionary.put("high_number_of_distinct_files_opened_attempts", numberOfDistinctFileOpenedUserIdFileHourly);
        dictionary.put("high_number_of_distinct_folders_opened_attempts", numberOfDistinctFolderOpenedUserIdFileHourly);
        dictionary.put("high_number_of_deletions", numberOfFileDeletedUserIdFileHourly);
        dictionary.put("high_number_of_successful_file_rename_operations", numberOfSuccessfulFileRenamedUserIdFileHourly);
        dictionary.put("high_number_of_file_move_operations", numberOfFileMovedUserIdFileHourly);
        dictionary.put("high_number_of_file_move_operations_from_shared_drive", numberOfFileMovedFromSharedDriveUserIdFileHourly);
        dictionary.put("high_number_of_file_move_operations_to_shared_drive", numberOfFileMovedToSharedDriveUserIdFileHourly);
        dictionary.put("abnormal_active_directory_day_time_operation", highestStartInstantScoreUserIdActiveDirectoryHourly);
        dictionary.put("abnormal_group_membership_sensitive_operation", sumOfHighestOperationTypeScoresUserIdGroupMembershipSecuritySensitiveActiveDirectoryHourly);
        dictionary.put("high_number_of_senesitive_group_membership_events", numberOfSensitiveGroupMembershipOperationUserIdActiveDirectoryHourly);
        dictionary.put("high_number_of_group_membership_events", numberOfGroupMembershipOperationUserIdActiveDirectoryHourly);
        dictionary.put("high_number_of_successful_object_change_operations", numberOfSuccessfulObjectManagementOperationUserIdActiveDirectoryHourly);
        dictionary.put("high_number_of_successful_user_change_security_sensitive_operations", numberOfSuccessfulUserManagementSecuritySensitiveOperationUserIdActiveDirectoryHourly);
        dictionary.put("high_number_of_failed_active_directory_events", numberOfFailedOperationTypeUserIdActiveDirectoryHourly);
        dictionary.put("admin_changed_his_own_password", adminPasswordChangedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_password_changed", nonAdminPasswordChangedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_account_enabled", userAccountEnabledScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_account_disabled", userAccountDisabledScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_account_unlocked", userAccountUnlockedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_account_type_changed", userAccountTypeChangedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_account_locked", userAccountLockedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_password_never_expires_option_changed", userPasswordNeverExpiresOptionChangedScoreUserIdActiveDirectoryHourly);
        dictionary.put("user_password_changed_by_non-owner", passwordChangedByNonOwnerScoreUserIdActiveDirectoryHourly);
//        dictionary.put("nested_member_added_to_critical_enterprise_group", nestedMemberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly);
        dictionary.put("member_added_to_critical_enterprise_group", memberAddedToCriticalEnterpriseGroupScoreUserIdActiveDirectoryHourly);
        dictionary.put("abnormal_logon_day_time", highestStartInstantScoreUserIdAuthenticationHourly);
        dictionary.put("abnormal_source_machine", sumOfHighestSrcMachineNameRegexClusterScoresUserIdAuthenticationHourly);
        dictionary.put("abnormal_destination_machine", sumOfHighestDstMachineNameRegexClusterScoresUserIdInteractiveRemoteAuthenticationHourly);
        dictionary.put("high_number_of_successful_authentications", numberOfSuccessfulAuthenticationsUserIdAuthenticationHourly);
        dictionary.put("high_number_of_failed_authentications", numberOfFailedAuthenticationsUserIdAuthenticationHourly);
        dictionary.put("high_number_of_distinct_destination_domains", numberOfDistinctDstMachineDomainUserIdAuthenticationHourly);
        dictionary.put("high_number_of_distinct_src_computers", numberOfDistinctSrcMachineIdUserIdAuthenticationHourly);
        dictionary.put("high_number_of_distinct_dst_computers", numberOfDistinctDstMachineIdUserIdAuthenticationHourly);
        dictionary.put("high_number_of_distinct_src_computer_clusters", numberOfDistinctSrcMachineNameRegexClusterUserIdAuthenticationHourly);
        dictionary.put("high_number_of_distinct_dst_computer_clusters", numberOfDistinctDstMachineNameRegexClusterUserIdAuthenticationHourly);
        dictionary.put("abnormal_object_change_operation", sumOfHighestOperationTypeScoresUserIdObjectManagementActiveDirectoryHourly);
        dictionary.put("high_number_of_distinct_sites", numberOfDistinctSiteUserIdAuthenticationHourly);
        dictionary.put("abnormal_site", sumOfHighestSiteScoresUserIdAuthenticationHourly);
    }


   public static String getFeatureName(String indicator) {
        return dictionary.get(indicator);
   }

}
