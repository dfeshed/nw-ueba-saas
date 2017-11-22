package presidio.data.generators.common.time;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.util.Assert;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * This {@link ITimeGenerator} returns {@link Instant}s from
 * a multiple time range, with a fixed intervals between them specified per range.
 *
 */
public class MultiRangeTimeGenerator implements ITimeGenerator {
    private final Instant startInstant;
    private final Instant endInstant;
    private List<ActivityRange> activityRanges;

    private Instant nextInstant;
    private ActivityRangeIterator activityRangeIterator;
    private int acivityRangesIndex;


    /**
     * C'tor.
     * @param startInstant   the start instant of the fixed time range (inclusive)
     * @param endInstant     the end instant of the fixed time range (exclusive)
     * @param activityRanges  list of start-end times of activity during a day and duration between events
     * @param defaultInterval for events frequency outside and between specified activityRanges
     *
     */
    public MultiRangeTimeGenerator(Instant startInstant, Instant endInstant, List<ActivityRange> activityRanges, Duration defaultInterval) {

        // validate provided parameters: start/end time pairs can't overlap
        // duration for each start/end time pair should be positive
        Assert.isTrue(startInstant.isBefore(endInstant), String.format("startInstant must be before endInstant. startInstant = %s, endInstant = %s.", startInstant, endInstant));

        Assert.isTrue(defaultInterval == null || (!defaultInterval.isNegative() && !defaultInterval.isZero()),
                String.format("interval should be positive. interval = %s.", defaultInterval));

        if(activityRanges == null){
            activityRanges = Collections.emptyList();
        }

        Assert.isTrue(defaultInterval != null || !activityRanges.isEmpty(), "defaultInterval may not be null since the activityRanges is empty.");

        this.startInstant = startInstant;
        this.endInstant = endInstant;


        //verify and build/set activityRanges
        if(!activityRanges.isEmpty()) {
            activityRanges.sort(Comparator.comparing(o -> o.startNanoOfADay));
            // verify that time ranges do not overlap
            ActivityRange prevActivityRange = null;
            for (ActivityRange curActivityRange: activityRanges){
                if(prevActivityRange!=null){
                    Assert.isTrue(prevActivityRange.endNanoOfADay <= curActivityRange.startNanoOfADay,
                            String.format("Activity Ranges overlap. ranges: %s", activityRanges));
                }
                prevActivityRange = curActivityRange;
            }
        }
        if(defaultInterval == null){
            this.activityRanges = activityRanges;
        } else{
            this.activityRanges = new ArrayList<>();
            long curNanoOfAday = 0;
            for(ActivityRange activityRange: activityRanges){
                if (curNanoOfAday < activityRange.startNanoOfADay){
                    this.activityRanges.add(new ActivityRange(LocalTime.ofNanoOfDay(curNanoOfAday), LocalTime.ofNanoOfDay(activityRange.startNanoOfADay), defaultInterval));
                }
                this.activityRanges.add(activityRange);
                curNanoOfAday = activityRange.endNanoOfADay;
            }
            if (curNanoOfAday < LocalTime.MAX.toNanoOfDay()){
                this.activityRanges.add(new ActivityRange(LocalTime.ofNanoOfDay(curNanoOfAday), LocalTime.MAX, defaultInterval));
            }
        }

        reset();
    }

    @Override
    public boolean hasNext() {
        return nextInstant.isBefore(endInstant);
    }

    @Override
    public Instant getNext() throws GeneratorException {

        if (hasNext()) {
            Instant returnedInstant = nextInstant;
            updateNextInstant();
            return returnedInstant;
        } else {
            throw new NoSuchElementException("There are no more instants.");
        }
    }

    private void updateNextInstant() {
        if (activityRangeIterator.hasNext()) {
            nextInstant = activityRangeIterator.getNext();
        } else {
            acivityRangesIndex++;
            if (acivityRangesIndex >= activityRanges.size()) {
                acivityRangesIndex = 0;
                nextInstant = nextInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
            }
            activityRangeIterator = new ActivityRangeIterator(activityRanges.get(acivityRangesIndex), nextInstant);
            nextInstant = activityRangeIterator.getNext();
        }
    }

    @Override
    public Instant getFirst() throws GeneratorException {
        return startInstant;
    }

    @Override
    public Instant getLast() throws GeneratorException {
        //return lastInstant;
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset() {
        // initialize nextInstant, activityRangeIterator and acivityRangesIndex
        for (acivityRangesIndex = 0; acivityRangesIndex < activityRanges.size(); acivityRangesIndex++){
            ActivityRange activityRange = activityRanges.get(acivityRangesIndex);
            if(!activityRange.isInstantAfterRange(startInstant)){
                break;
            }
        }
        if(acivityRangesIndex < activityRanges.size()){
            activityRangeIterator = new ActivityRangeIterator(activityRanges.get(acivityRangesIndex), startInstant);
        } else{
            //advance to the next day.
            acivityRangesIndex = 0;
            activityRangeIterator = new ActivityRangeIterator(activityRanges.get(acivityRangesIndex),
                    startInstant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS));
        }
        nextInstant = activityRangeIterator.getNext();
    }

    public static class ActivityRange {

        private long startNanoOfADay;
        private long endNanoOfADay;
        private Duration duration;

        public ActivityRange(LocalTime start, LocalTime end, Duration duration ) {

            startNanoOfADay = start.toNanoOfDay();
            endNanoOfADay = end.toNanoOfDay();
            this.duration = duration;

            if (duration.compareTo(Duration.ZERO) <= 0) {
                throw new IllegalArgumentException(String.format("interval " +
                        "must be positive. interval = %s.", duration));
            }
        }

        public boolean isInstantAfterRange(Instant instant){
            Instant rangeEnd = getRangeEnd(instant);

            return (instant.isAfter(rangeEnd) || instant.equals(rangeEnd));
        }

        public boolean isInstantBeforeRange(Instant instant){
            Instant rangeStart = getRangeStart(instant);

            return (instant.isBefore(rangeStart));
        }

        public Instant getRangeStart(Instant instant){
            return instant.truncatedTo(ChronoUnit.DAYS).plus(startNanoOfADay,ChronoUnit.NANOS);
        }

        public Instant getRangeEnd(Instant instant){
            return instant.truncatedTo(ChronoUnit.DAYS).plus(endNanoOfADay,ChronoUnit.NANOS);
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }

    public static class ActivityRangeIterator{
        private Instant nextInstant;
        private Instant endInstant;
        private Duration interval;

        public ActivityRangeIterator(ActivityRange activityRange, Instant instant){
            if(activityRange.isInstantBeforeRange(instant)){
                nextInstant = activityRange.getRangeStart(instant);
                endInstant = activityRange.getRangeEnd(instant);
            } else if(activityRange.isInstantAfterRange(instant)){
                instant = instant.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);
                nextInstant = activityRange.getRangeStart(instant);
                endInstant = activityRange.getRangeEnd(instant);
            } else {
                nextInstant = instant;
                endInstant = activityRange.getRangeEnd(instant);
            }
            this.interval = activityRange.duration;
        }

        public boolean hasNext(){
            return nextInstant != null;
        }

        public Instant getNext(){
            Instant ret = nextInstant;
            nextInstant = nextInstant.plus(interval);
            if(!nextInstant.isBefore(endInstant)){
                nextInstant = null;
            }
            return ret;
        }
    }
}
