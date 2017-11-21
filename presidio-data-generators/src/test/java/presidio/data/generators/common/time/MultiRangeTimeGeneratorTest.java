package presidio.data.generators.common.time;

import org.junit.Assert;
import org.junit.Test;
import presidio.data.generators.common.GeneratorException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiRangeTimeGeneratorTest {

    @Test
    public void test_generator_with_both_activity_range_list_and_default_interval() throws GeneratorException {

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(1,0), LocalTime.of(2,0), Duration.ofSeconds(600)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(2,0), LocalTime.of(4,0), Duration.ofMinutes(15)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,0), LocalTime.of(16,0), Duration.ofHours(2)));

        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1,ChronoUnit.DAYS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                rangesList, Duration.ofHours(1));
        int count = 0;
        while (TG.hasNext()) {
            System.out.print(++count + " ");
            System.out.println(TG.getNext().toString());
        }
        Assert.assertEquals(60, count);
    }

    @Test
    public void test_generator_with_empty_activity_range_list_and_default_interval() throws GeneratorException {
        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1,ChronoUnit.DAYS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                Collections.emptyList(), Duration.ofHours(1));
        int count = 0;
        while (TG.hasNext()) {
            System.out.print(++count + " ");
            System.out.println(TG.getNext().toString());
        }
        Assert.assertEquals(48, count);
    }

    @Test
    public void test_generator_without_default_interval() throws GeneratorException {

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(1,0), LocalTime.of(2,0), Duration.ofSeconds(600)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(2,0), LocalTime.of(4,1), Duration.ofMinutes(15)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,0), LocalTime.of(16,1), Duration.ofHours(2)));

        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1,ChronoUnit.DAYS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                rangesList, null);
        int count = 0;
        while (TG.hasNext()) {
            System.out.print(++count + " ");
            System.out.println(TG.getNext().toString());
        }
        Assert.assertEquals(42, count);
    }

    @Test
    public void test_non_logical_activity_range_configuration() throws GeneratorException {

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(1,0), LocalTime.of(2,0), Duration.ofSeconds(600)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(2,20), LocalTime.of(3,40), Duration.ofMinutes(15)));
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(6,22), LocalTime.of(16,40), Duration.ofMinutes(60)));

        ITimeGenerator TG = new MultiRangeTimeGenerator(Instant.now().truncatedTo(ChronoUnit.DAYS).minus(1,ChronoUnit.DAYS),Instant.now().truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS),
                rangesList, Duration.ofMinutes(30));
        int count = 0;
        while (TG.hasNext()) {
            System.out.print(++count + " ");
            System.out.println(TG.getNext().toString());
        }
        Assert.assertEquals(94, count);
    }
}
