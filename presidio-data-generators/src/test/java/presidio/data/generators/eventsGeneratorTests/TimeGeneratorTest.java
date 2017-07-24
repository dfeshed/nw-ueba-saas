package presidio.data.generators.eventsGeneratorTests;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.FloatingTimeGenerator;
import presidio.data.generators.common.time.TimeGenerator;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class TimeGeneratorTest {

    @Test
    public void DebugTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 7;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);

        TimeGenerator TG = new TimeGenerator(startTime,endTime,interval, daysBackFrom, daysBackTo);
        Assert.assertTrue(TG.hasNext());
    }

    @Test
    public void IntervalHasNextTest() throws GeneratorException {
        int startHour = 8;      int startMin = 0;
        int endHour = 9;        int endMin = 0;
        int daysBackFrom = 5;   int daysBackTo = 0;
        int interval = 55;

        LocalTime startTime = LocalTime.of(startHour,startMin);
        LocalTime endTime = LocalTime.of(endHour,endMin);

        TimeGenerator TG = new TimeGenerator(startTime,endTime,interval, daysBackFrom, daysBackTo);
        Assert.assertTrue(TG.hasNext());
    }

    @Test
    public void TimeGeneratorWithShiftTest() throws GeneratorException {
        TimeGenerator TG =
            new FloatingTimeGenerator(LocalTime.of(0, 0), LocalTime.of(1, 0), 25, 3, 1);

        Assert.assertEquals("00:00:00", TG.getNext().toString().substring(11,19));
        Assert.assertEquals("00:25:00", TG.getNext().toString().substring(11,19));
        Assert.assertEquals("00:50:00", TG.getNext().toString().substring(11,19));
    }

    //@Test
    public void DefaultTimeGeneratorWithShiftTest() throws GeneratorException {
        TimeGenerator myTimeGenerator =
                new FloatingTimeGenerator();
        myTimeGenerator.printIntervals();
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

        TimeGenerator TG = new TimeGenerator(startTime, endTime,interval, daysBackFrom, daysBackTo);
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
        TimeGenerator TG = new TimeGenerator(startTime, endTime,interval, daysBackFrom, daysBackTo);

        for (int i = 0; i<TG.getSize(); i++) TG.getNext();
        TG.getNext();

    }

}
