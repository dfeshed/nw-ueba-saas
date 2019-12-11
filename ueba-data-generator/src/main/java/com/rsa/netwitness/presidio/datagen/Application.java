package com.rsa.netwitness.presidio.datagen;

import com.rsa.netwitness.presidio.datagen.scenarios.AlertsDataScenario;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class Application {
    public static Properties APPLICATION_PROPERTIES;

    public static void main(String[] args) {
        System.out.println("*************************  STARTED  *************************");
        String propString = String.join("\n", args);

        // Add properties:
        APPLICATION_PROPERTIES = parsePropertiesString(propString);
        System.out.println(" +++  Parameters:\n");
        APPLICATION_PROPERTIES.forEach((key, value) -> System.out.println(key.toString().concat("=").concat(value.toString())));
        APPLICATION_PROPERTIES.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));

        // Run scenario:
        TestNG testSuite = new TestNG();
        testSuite.setTestClasses(new Class[] { AlertsDataScenario.class });
        testSuite.addListener(new TestListenerAdapter());
        testSuite.setDefaultSuiteName("My Test Suite");
        testSuite.setDefaultTestName("My Test");
        testSuite.run();

        System.out.println("*************************  DONE  *************************");
    }

    private static Properties parsePropertiesString(String s) {
        final Properties p = new Properties();
        try {
            p.load(new StringReader(s));
        } catch (IOException e) {
            System.err.println("Unable to parse properties from the String: " + s);
        }
        return p;
    }

}
