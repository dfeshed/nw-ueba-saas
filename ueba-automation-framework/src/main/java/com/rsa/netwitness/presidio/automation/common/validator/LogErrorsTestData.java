package com.rsa.netwitness.presidio.automation.common.validator;

import org.testng.Assert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LogErrorsTestData {
    private String command;
    public LogErrorsTestData(String command) {
        this.command = command;
    }
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }

    public void verifyLogs() {
        String command = String.format(getCommand());
        System.out.println(command);

        try {
            // run grep command to scan for logs
            Process p = Runtime.getRuntime().exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(p.getInputStream()));

            // read the output from the command
            int i = 0;
            String logErrors = "";
            String sOut;
            while ((sOut = stdInput.readLine()) != null) {
                i++;
                logErrors += sOut + "\n";
            }
            Assert.assertEquals(i, 0, "Errors found in log. Command: <<" + command + ">> \n" + logErrors);

            // read any errors from the attempted command
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
            String sErr;
            String commandError = "";
            while ((sErr = stdError.readLine()) != null) {
                commandError += sErr;
            }
            System.out.println("Error when running log scan: " + commandError);

        } catch (IOException e) {
            System.out.println("Exception when running log scan:" + e.getMessage());
        }
    }

    public static Object[][] getE2ELogsData() {
        return new Object[][]{
                {new LogErrorsTestData("sudo grep ERROR -l -R /var/log/elasticsearch/")},
                {new LogErrorsTestData("sudo grep Exception -l -R /var/log/elasticsearch/")},
                {new LogErrorsTestData("sudo grep ERROR -i /var/log/mongodb/mongod.log")},
                //Looking for "ERROR" but not "t ERROR" (part of "not ERROR" prints from flume). Need to think how to improve
                {new LogErrorsTestData("sudo grep -E -l -R '[^t] ERROR' /var/log/netwitness/presidio/3p/airflow/logs/")},
                {new LogErrorsTestData("sudo grep error -l -R /var/log/nginx/")}
        };
    }

    public static Object[][] getCommonLogsData() {
        return new Object[][]{
                {new LogErrorsTestData("sudo grep ERROR -l -R /var/log/elasticsearch/")},
                {new LogErrorsTestData("sudo grep ERROR -i /var/log/mongodb/mongod.log")},
        };
    }

}
