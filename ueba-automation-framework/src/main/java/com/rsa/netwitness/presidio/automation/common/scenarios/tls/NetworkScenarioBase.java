package com.rsa.netwitness.presidio.automation.common.scenarios.tls;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;

import static java.lang.String.valueOf;

abstract class NetworkScenarioBase {

    protected int startHourOfDay = 8;
    protected int endHourOfDay = 17;
    protected int daysBackFrom = 30;
    protected int daysBackTo = 0;
    protected int startHourOfDayAnomaly = 8;
    protected int endHourOfDayAnomaly = 17;
    protected int daysBackFromAnomaly = 1;
    protected int daysBackToAnomaly = 0;
    protected int intervalMinutes = 10;
    protected int intervalMinutesAnomaly = 30;


    abstract String getScenarioName();

    protected String Ja3Entity(int index)  {
        return createEntity(index, "ja3");
    }

    protected String SSLSubjEntity(int index)  {
        return createEntity(index, "ssl_subj");
    }

    private String createEntity(int index, String fieldName)  {
        return getScenarioName().concat("_").concat(fieldName).concat("_").concat(valueOf(index));
    }

    protected ITimeGenerator getDefaultRegularTimeGen() {
        return getTimeGen(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, intervalMinutes);
    }

    protected ITimeGenerator getDefaultUncommonTimeGen() {
        return getTimeGen(startHourOfDayAnomaly, endHourOfDayAnomaly, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getDefaultUnregularHoursTimeGen() {
        return getTimeGen(2, 5, daysBackFromAnomaly, daysBackToAnomaly, intervalMinutesAnomaly);
    }

    protected ITimeGenerator getTimeGen(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, int intervalMinutes) {
        try {
            return new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo-1, intervalMinutes)
                    .createTimeGenerator();
        } catch (GeneratorException e) {
            e.printStackTrace();
        }
        return null;
    }


}

