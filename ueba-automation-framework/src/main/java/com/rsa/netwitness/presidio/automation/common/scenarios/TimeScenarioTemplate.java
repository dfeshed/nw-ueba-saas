package com.rsa.netwitness.presidio.automation.common.scenarios;

import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.LocalTime;

/**
 * Created by presidio on 8/22/17.
 */
public class TimeScenarioTemplate {
    public static ITimeGenerator getNormalTimeGenerator() throws GeneratorException {
        // create normal events
        return new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(16, 0), 30, 30, 1);
    }

    public static ITimeGenerator getMinSamplesTimeGenerator() throws GeneratorException {
        // create normal events, only 28 - one event per day
        return new MinutesIncrementTimeGenerator(LocalTime.of(8, 0), LocalTime.of(9, 0), 60, 30, 2);
    }
}
