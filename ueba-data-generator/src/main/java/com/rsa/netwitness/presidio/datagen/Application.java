package com.rsa.netwitness.presidio.datagen;

import com.rsa.netwitness.presidio.datagen.scenarios.AlertsDataScenario;
import org.testng.TestListenerAdapter;
import org.testng.TestNG;

public class Application {

    public static void main(String[] args) {
        System.out.println("*************************  STARTED  *************************");

        TestNG testSuite = new TestNG();
        testSuite.setTestClasses(new Class[] { AlertsDataScenario.class });
        testSuite.addListener(new TestListenerAdapter());
        testSuite.setDefaultSuiteName("My Test Suite");
        testSuite.setDefaultTestName("My Test");
        testSuite.run();
    }

}
