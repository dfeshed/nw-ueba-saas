package com.rsa.netwitness.presidio.automation.test.mapping;

import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import org.testng.annotations.DataProvider;

public class ExpectedAuthenticationEvents {

    @DataProvider(name = "expectedEvents")
    public Object[][] getExpectedEvents() {
        return new Object[][]{

                // 4624
                {"4624_INTERACTIVE", new AdapterAuthenticationStoredData.Builder().eventId("EV-1642-e2e_auth").userId("e2e_auth_user2").operationType("INTERACTIVE").result("SUCCESS").userName("e2e_auth_user2").userDisplayName("e2e_auth_user2").srcMachineId("host_1").srcMachineName("host_1").build()},
                {"4624_REMOTE_INTERACTIVE", new AdapterAuthenticationStoredData.Builder().eventId("EV-1661-e2e_auth").userId("e2e_auth_user2").operationType("REMOTE_INTERACTIVE").result("SUCCESS").userName("e2e_auth_user2").userDisplayName("e2e_auth_user2").dstMachineId("host_2").dstMachineName("host_2").build()},

                // 4625
                {"4625_INTERACTIVE", new AdapterAuthenticationStoredData.Builder().eventId("EV-3611-e2e_auth").userId("e2e_auth_user3").operationType("INTERACTIVE").result("FAILURE").userName("e2e_auth_user3").userDisplayName("e2e_auth_user3").srcMachineId("host_3").srcMachineName("host_3").build()},
                {"4625_REMOTE_INTERACTIVE", new AdapterAuthenticationStoredData.Builder().eventId("EV-3607-e2e_auth").userId("e2e_auth_user3").operationType("REMOTE_INTERACTIVE").result("FAILURE").userName("e2e_auth_user3").userDisplayName("e2e_auth_user3").dstMachineId("host_2").dstMachineName("host_2").build()},

                //SECURE_ID
                {"SECURE_ID", new AdapterAuthenticationStoredData.Builder().eventId("EV-1643-e2e_auth").dataSource("rsaacesrv").userId("e2e_auth_user2").operationType("MFA").result("SUCCESS").userName("e2e_auth_user2").userDisplayName("e2e_auth_user2").srcMachineId("host_2").srcMachineName("host_2").build()}

        };
    }
}
