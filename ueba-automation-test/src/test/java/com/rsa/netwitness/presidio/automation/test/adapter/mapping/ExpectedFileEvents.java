package com.rsa.netwitness.presidio.automation.test.adapter.mapping;

import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import org.testng.annotations.DataProvider;

public class ExpectedFileEvents {

    @DataProvider(name = "expectedEvents")
    public Object[][] getExpectedEvents() {
        return new Object[][]{


                // 4663
                {"4663", new AdapterFileStoredData.Builder().eventId("EV-1-file_optypes_and_categories").userId("file_optypes_and_categories").operationType("FILE_CREATED").result("SUCCESS").userName("file_optypes_and_categories").userDisplayName("file_optypes_and_categories").srcFilePath("File.jar").isSrcDriveShared(false).build()},

                // 4660
                {"4660", new AdapterFileStoredData.Builder().eventId("EV-11-file_optypes_and_categories").userId("file_optypes_and_categories").operationType("FILE_DELETED").result("SUCCESS").userName("file_optypes_and_categories").isSrcDriveShared(false).build()},

                // 5145
                {"5145", new AdapterFileStoredData.Builder().eventId("EV-97-file_optypes_and_categories").userId("file_optypes_and_categories").operationType("FILE_OPENED").result("SUCCESS").userName("file_optypes_and_categories").srcFilePath("/usr/someuser/somesubdir/1/File.dmg").isSrcDriveShared(false).build()},

                // FAILED
// need to rewrite or fix
//                {"4663_FAILED", new AdapterFileStoredData.Builder().eventId("EV-241-file_qa_1_101").userId("file_qa_1_101").operationType("FILE_DELETED").result("SUCCESS").userName("file_qa_1_101").userDisplayName("file_qa_1_101").srcFilePath("File.jar").isSrcDriveShared(false).build()},

        };
    }
}
