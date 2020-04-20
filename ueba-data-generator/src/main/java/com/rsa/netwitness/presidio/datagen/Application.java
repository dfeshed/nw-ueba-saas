package com.rsa.netwitness.presidio.datagen;

import com.rsa.netwitness.presidio.datagen.scenarios.AlertsDataScenario;
import com.rsa.netwitness.presidio.datagen.scenarios.AlertsDataScenarioNoSpring;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {
        System.out.println("*************************  STARTED  *************************");
        String propString = String.join("\n", args);

        // Add properties:
        Properties APPLICATION_PROPERTIES = parsePropertiesString(propString);
        System.out.println(" +++++  Properties to set  +++++");
        APPLICATION_PROPERTIES.forEach((key, value) -> System.out.println(key.toString().concat("=").concat(value.toString())));
        APPLICATION_PROPERTIES.forEach((key, value) -> System.setProperty(key.toString(), value.toString()));
        System.out.println(" +++++++++++++++++++++++++++++++");

        // Run scenario:
        TestNG testSuite = new TestNG();

        if (APPLICATION_PROPERTIES.get("generatorFormat").equals("MONGO_ADAPTER")) {
            testSuite.setTestClasses(new Class[] { AlertsDataScenario.class });
        } else {
            testSuite.setTestClasses(new Class[] { AlertsDataScenarioNoSpring.class });
        }

        testSuite.addListener(new TestListenerAdapter());
        testSuite.setDefaultSuiteName("UEBA Events generator");
        testSuite.setDefaultTestName("UEBA Events generator");
        testSuite.run();

        System.out.println("*************************  DONE  *************************");
        System.exit(0);
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
