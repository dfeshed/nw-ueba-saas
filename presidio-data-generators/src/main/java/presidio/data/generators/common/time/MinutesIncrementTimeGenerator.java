package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.LocalTime;

public class MinutesIncrementTimeGenerator extends TimeGenerator {

    private static final int MINUTES_TO_MILLIS = 60*1000;

    private static int minutes2Millis(int intervalInMinutes) {
        return intervalInMinutes * MINUTES_TO_MILLIS;

    }

    public MinutesIncrementTimeGenerator() throws GeneratorException {
        super(LocalTime.of(8,0), LocalTime.of(16,0), minutes2Millis(10), 30, 1);
    }

    public MinutesIncrementTimeGenerator(LocalTime startLocalTime, LocalTime endLocalTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {
        super(startLocalTime, endLocalTime, minutes2Millis(interval), daysBackFrom, daysBackTo);
    }

}
