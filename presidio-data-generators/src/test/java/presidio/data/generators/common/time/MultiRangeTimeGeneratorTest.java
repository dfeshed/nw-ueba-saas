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
    public void MultiRangeTimeGeneratorDebugTest() throws GeneratorException {

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(12,0), LocalTime.of(20,0), Duration.ofSeconds(60)));

        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).plus(11, ChronoUnit.HOURS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                rangesList, Duration.ofMinutes(30));
        while (TG.hasNext()) System.out.println(TG.getNext().toString());
        Assert.assertTrue(true);
    }

}
