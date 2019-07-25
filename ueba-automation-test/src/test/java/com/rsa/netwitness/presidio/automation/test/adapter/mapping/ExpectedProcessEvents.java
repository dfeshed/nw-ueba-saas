package com.rsa.netwitness.presidio.automation.test.adapter.mapping;

import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import org.testng.annotations.DataProvider;

public class ExpectedProcessEvents {

    @DataProvider(name = "expectedEvents")
    public Object[][] getExpectedEvents() {
        return new Object[][]{

                // OPEN_PROCESS
                {"time_anomaly", new AdapterProcessStoredData.Builder()
                        .eventId            ("EV-1-e2e_process_alerts")     //event_source_id
                        .dataSource         ("Netwitness Endpoint")         //device.type
                        .userId             ("e2e_process_time_anomaly")    //user.src
//                        .userName           ("e2e_process_time_anomaly")    //user.src
//                        .userDisplayName    ("e2e_process_time_anomaly")    //user.src
//                        .operationType      ("OPEN_PROCESS")                //action
//                        .machineId          ("host_1")                      //alias.host
//                        .machineName        ("host_1")                      //alias.host
////                        .machineOwner       ("e2e_process_time_anomaly")    //owner
//                        .srcProcessDirectory("C:\\Windows\\System32")       //directory.src
//                        .srcProcessFileName ("cmd.exe")                     //filename.src
////                        .srcProcessDirectoryGroups("")                        //context.target=dir.?
//                        .srcProcessCategories("")                           //?
//                        .srcProcessCertificateIssuer("")                    //cert.common
//                        .dstProcessDirectory("C:\\Windows\\System32")       //directory.src
//                        .dstProcessFileName ("cmd.exe")                     //filename.src
//                        .dstProcessDirectoryGroups("")                        //context.target=dir.?
//                        .dstProcessCategories("")                           //?
//                        .dstProcessCertificateIssuer("")                    //cert.common
//                        .build()
},
        };
    }
}
