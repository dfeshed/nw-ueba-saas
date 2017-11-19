package presidio.data.generators.common.time;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;

public class MultiRangeTimeGeneratorTest {

    @Test
    public void MultiRangeTimeGeneratorTest() throws GeneratorException {

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(1,0), LocalTime.of(2,0), Duration.ofSeconds(600)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(2,20), LocalTime.of(3,40), Duration.ofMinutes(15)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,22), LocalTime.of(16,40), Duration.ofMinutes(60)));

        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(0, ChronoUnit.HOURS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                rangesList, Duration.ofMinutes(30));
        int count = 0;
        while (TG.hasNext()) {
            System.out.print(++count + " ");
            System.out.println(TG.getNext().toString());
        }
        Assert.assertEquals(45, count);
    }

}
