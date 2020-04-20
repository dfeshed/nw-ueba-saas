package com.rsa.netwitness.presidio.automation.test.ade;

import com.rsa.netwitness.presidio.automation.common.validator.LogErrorsTestData;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;

import java.io.IOException;

import static com.rsa.netwitness.presidio.automation.common.validator.LogErrorsTestData.getCommonLogsData;

public class LogsErrors_Test extends AbstractTestNGSpringContextTests {

    @DataProvider(name = "testParams")
    public Object[][] getData() {
        return getCommonLogsData();
    }

    // TODO: redirect sout from each presidio application to files and scan them for errors
    //@Test(dataProvider = "testParams")
    public void logVerifications(LogErrorsTestData data) throws IOException {
        data.verifyLogs();
    }

}