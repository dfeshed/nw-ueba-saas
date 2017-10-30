package presidio.ade.test.utils.generators;

import fortscale.utils.fixedduration.FixedDurationStrategy;
import org.testng.collections.Lists;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.data.generators.common.CustomStringGenerator;
import presidio.data.generators.common.CyclicMapGenerator;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringRegexCyclicValuesGenerator;
import presidio.data.generators.common.time.TimeGenerator;
import presidio.data.generators.event.IEventGenerator;

import java.time.Instant;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * generates hourly accumulated events
 * Created by barak_schuster on 9/4/17.
 */
public class AccumulatedAggregationFeatureRecordHourlyGenerator implements IEventGenerator<AccumulatedAggregationFeatureRecord> {
    private IStringGenerator contextIdGenerator;
    private TimeGenerator startInstantGenerator;
    private IStringGenerator featureNameGenerator;
    private CyclicMapGenerator<Integer, Double> aggregatedFeatureValuesGenerator;


    public AccumulatedAggregationFeatureRecordHourlyGenerator(String featureName, String contextIdPattern,
                                                              Map<Integer, Double> aggregatedFeatureValuesMap,
                                                              int startHourOfDay, int endHourOfDay) throws GeneratorException {
        this.featureNameGenerator = new CustomStringGenerator(featureName);
        this.startInstantGenerator = new TimeGenerator(LocalTime.of(startHourOfDay, 0), LocalTime.of(endHourOfDay, 0), 60, 30, 1);
        this.contextIdGenerator = new StringRegexCyclicValuesGenerator(contextIdPattern);

        this.aggregatedFeatureValuesGenerator = new CyclicMapGenerator<>(Lists.newArrayList(aggregatedFeatureValuesMap));

    }


    @Override
    public List<AccumulatedAggregationFeatureRecord> generate() throws GeneratorException {

        List<AccumulatedAggregationFeatureRecord> evList = new ArrayList<>();

        // fill list of events
        while (startInstantGenerator.hasNext()) {
            Instant startInstant = startInstantGenerator.getNext();
            Instant endInstant = startInstant.plus(FixedDurationStrategy.HOURLY.toDuration());
            String contextId = contextIdGenerator.getNext();
            String featureName = featureNameGenerator.getNext();
            Map<Integer, Double> aggregatedFeatureValues = aggregatedFeatureValuesGenerator.getNext();
            AccumulatedAggregationFeatureRecord record = new AccumulatedAggregationFeatureRecord(startInstant, endInstant, contextId, featureName);
            record.setAggregatedFeatureValues(aggregatedFeatureValues);

            evList.add(record);
        }
        return evList;
    }
}