package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.LocalTime;

public class FloatingTimeGenerator extends TimeGenerator implements ITimeGenerator {

    public FloatingTimeGenerator() throws GeneratorException {
        super();
    }
    public FloatingTimeGenerator(LocalTime startTime, LocalTime endTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {
        super(startTime, endTime, interval, daysBackFrom, daysBackTo);
    }

    @Override
    public void buildTimeList(LocalTime startTime, LocalTime endTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {
        super.doShifting(true);
        super.buildTimeList(startTime, endTime, interval, daysBackFrom, daysBackTo);
    }
}
