package com.rsa.netwitness.presidio.automation.test.adapter.mapping;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import org.testng.annotations.DataProvider;

public class ExpectedActiveDirectoryEvents {

    @DataProvider(name = "expectedEvents")
    public Object[][] getExpectedEvents() {
        return new Object[][]{

                // 4741
                {"4741", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-1-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("COMPUTER_ACCOUNT_CREATED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4742
                {"4742", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-2-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("COMPUTER_ACCOUNT_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4733
                {"4733", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-3-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_REMOVED_FROM_SECURITY_ENABLED_LOCAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4734
                {"4734", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-4-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_LOCAL_GROUP_DELETED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},

                //4735 - disabled, according to Guy is not helpful for UEBA
                //{"4735", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-5-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_LOCAL_GROUP_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test5").build()},

                //4755 - disabled, according to Guy is not helpful for UEBA
                //{"4755", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-6-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4740
                {"4740", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-7-ad_optypes_and_categories").userId("test2").operationType("USER_ACCOUNT_LOCKED").result("SUCCESS").userName("Test2").objectId(null).build()},

                //4794
                {"4794", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-8-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("ATTEMPT_MADE_TO_SET_DIRECTORY_SERVICES_RESTORE_MODE_ADMINISTRATOR_PASSWORD").result("SUCCESS").userName("ad_optypes_and_categories").objectId(null).build()},

                //5376
                {"5376", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-9-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("CREDENTIAL_MANAGER_CREDENTIALS_BACKED_UP").result("SUCCESS").userName("ad_optypes_and_categories").objectId(null).build()},

                //5377
                {"5377", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-10-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("CREDENTIAL_MANAGER_CREDENTIALS_RESTORED_FROM_BACKUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId(null).build()},

                //5136
                {"5136", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-11-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("DIRECTORY_SERVICE_OBJECT_MODIFIED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4764
                {"4764", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-12-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("GROUP_TYPE_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4670 - disabled, according to Guy is not helpful for UEBA
                //{"4670", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-13-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("PERMISSIONS_ON_OBJECT_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4743
                {"4743", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-14-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("COMPUTER_ACCOUNT_DELETED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},

                //4739
                {"4739", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-15-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("DOMAIN_POLICY_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId(null).build()},

                //4737 - disabled, according to Guy is not helpful for UEBA
                //{"4737", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-16-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_GLOBAL_GROUP_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4727
                {"4727", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-17-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_GLOBAL_GROUP_CREATED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4728
                {"4728", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-18-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_ADDED_TO_SECURITY_ENABLED_GLOBAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4754
                {"4754", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-19-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_UNIVERSAL_GROUP_CREATED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},

                //4756
                {"4756", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-20-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test5").build()},

                //4757
               // {"4757", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-21-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_ADDED_TO_SECURITY_ENABLED_UNIVERSAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test21").build()},

                //4758
                {"4758", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-21-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_UNIVERSAL_GROUP_DELETED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4720
                {"4720", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-22-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_CREATED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4722
                {"4722", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-23-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_ENABLED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4723
                {"4723", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-24-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_PASSWORD_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},

                //4724
                {"4724", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-25-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_PASSWORD_RESET").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test5").build()},

                //4725
                {"4725", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-26-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_DISABLED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4726
                {"4726", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-27-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_DELETED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4738
                {"4738", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-28-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_CHANGED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4767
                {"4767", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-29-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("USER_ACCOUNT_UNLOCKED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},

                //4717
                {"4717", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-30-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SYSTEM_SECURITY_ACCESS_GRANTED_TO_ACCOUNT").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test5").build()},

                //4729
                {"4729", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-31-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_REMOVED_FROM_SECURITY_ENABLED_GLOBAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test1").build()},

                //4730
                {"4730", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-32-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_GLOBAL_GROUP_DELETED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test2").build()},

                //4731
                {"4731", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-33-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("SECURITY_ENABLED_LOCAL_GROUP_CREATED").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test3").build()},

                //4732
                {"4732", new AdapterActiveDirectoryStoredData.Builder().eventId("EV-34-ad_optypes_and_categories").userId("ad_optypes_and_categories").operationType("MEMBER_ADDED_TO_SECURITY_ENABLED_LOCAL_GROUP").result("SUCCESS").userName("ad_optypes_and_categories").objectId("Test4").build()},


        };
    }
}
