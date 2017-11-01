package presidio.data.generators.common.time;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by YaronDL on 8/8/2017.
 */
public class TimeGeneratorTest {
    @Test
    public void DebugTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 7;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);

        MinutesIncrementTimeGenerator TG = new MinutesIncrementTimeGenerator(startTime,endTime,interval, daysBackFrom, daysBackTo);
        Assert.assertTrue(TG.hasNext());
    }

    @Test
    public void MillisIncrementDebugTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 436;

        LocalTime startTime = LocalTime.of(startHour,startMin, 20, 115);
        LocalTime endTime = LocalTime.of(endHour,endMin, 20, 500);

        ITimeGenerator TG = new TimeGenerator(startTime,endTime,interval, daysBackFrom, daysBackTo);
        while (TG.hasNext()) System.out.println(TG.getNext().toString());
        Assert.assertTrue(true);
    }

    @Test
    public void IntervalHasNextTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 55;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);

        MinutesIncrementTimeGenerator TG = new MinutesIncrementTimeGenerator(startTime,endTime,interval, daysBackFrom, daysBackTo);
        Assert.assertTrue(TG.hasNext());
    }

    @Test
    public void IntervalGetNextTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 55;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);

        String expected = LocalDate.now(Clock.systemUTC()).minus(daysBackFrom, ChronoUnit.DAYS).toString() + "T"
                + LocalTime.of(startHour, startMin).toString() + ":00Z";

        MinutesIncrementTimeGenerator TG = new MinutesIncrementTimeGenerator(startTime, endTime,interval, daysBackFrom, daysBackTo);
        Assert.assertEquals(TG.getNext().toString(), expected);
    }

    @Test (expected = GeneratorException.class)
    public void TimeGeneratorExeptionTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 55;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);
        MinutesIncrementTimeGenerator TG = new MinutesIncrementTimeGenerator(startTime, endTime,interval, daysBackFrom, daysBackTo);

        for (int i = 0; i<TG.getSize(); i++) TG.getNext();
        TG.getNext();

    }

}
