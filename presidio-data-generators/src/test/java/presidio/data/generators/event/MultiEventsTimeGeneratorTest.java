package presidio.data.generators.event;

import org.testng.Assert;
import org.testng.annotations.Test;
import presidio.data.domain.event.Event;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.time.MultiRangeTimeGenerator;
import presidio.data.generators.event.file.FileEventsGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MultiEventsTimeGeneratorTest {

    @Test
    public void MultiEventsGenerator_Test() throws GeneratorException {
        /** 2 DAYS **/
        Instant startInstant = Instant.now().minus(3, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant endInstant = Instant.now().minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        /** EVENTS, just using RandomMultiEventsGenerator on one schema for simplicity **/
        FileEventsGenerator fileEventsGenerator = new FileEventsGenerator();
        List< RandomMultiEventGenerator.EventGeneratorProbability > listOfProbabilities = new ArrayList<>();
        RandomMultiEventGenerator.EventGeneratorProbability fileEventsProbability =
                new RandomMultiEventGenerator.EventGeneratorProbability(fileEventsGenerator, 1);
        listOfProbabilities.add(fileEventsProbability);

        List<MultiRangeTimeGenerator.ActivityRange> rangesList = new ArrayList<>();
        /** Just using one range from 2:00-22:00, interval -1h **/
        rangesList.add(new MultiRangeTimeGenerator.ActivityRange(LocalTime.of(2,0), LocalTime.of(22,0), Duration.ofMinutes(60)));

        /** default interval - 2h**/
        RandomMultiEventGenerator randomEventsGenerator = new RandomMultiEventGenerator(listOfProbabilities,
                startInstant, endInstant, rangesList, Duration.ofMinutes(120));

        /**
         * Trying to generate bulk of 2000 events, when actually should be less events
         * 2 days
         * Activity Range: 20h in a day, 1 ev per h,
         * default range: 4h in a day, 1 ev per 2h
         *
         * Expected generated events - 44
         *
         * Actual size of returned list - 2000
         * events from 44 to 1999 are NULLs
         * **/
        while (randomEventsGenerator.hasNext()) {
            // for some reason, in time generator, after 44 events, nextInstant is set for default -30d (from AbstractEventGenerator) and hasNext returns true.
            List<Event> events = randomEventsGenerator.generate(2000);
            Assert.assertEquals(events.size(),44);
        }
    }

}
