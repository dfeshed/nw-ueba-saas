package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.LocalTime;

/**
 * Created by YaronDL on 10/22/2017.
 */
public class SingleTimeGeneratorFactory implements ITimeGeneratorFactory{
    private static final int INTERVAL_DEFAULT = 10;
    private static final int MINUTE_DEFAULT = 0;
    private int startHourOfDay;
    private int endHourOfDay;
    private int daysBackFrom;
    private int daysBackTo;


    public SingleTimeGeneratorFactory(int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo){
        this.startHourOfDay = startHourOfDay;
        this.endHourOfDay = endHourOfDay;
        this.daysBackFrom = daysBackFrom;
        this.daysBackTo = daysBackTo;
    }

    public ITimeGenerator createTimeGenerator() throws GeneratorException {
        return new TimeGenerator(LocalTime.of(startHourOfDay, MINUTE_DEFAULT), LocalTime.of(endHourOfDay, MINUTE_DEFAULT), INTERVAL_DEFAULT, daysBackFrom, daysBackTo);
    }
}
