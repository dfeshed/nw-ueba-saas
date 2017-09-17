package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimeGenerator implements ITimeGenerator {
    private List<Instant> eventTimes = new ArrayList<>();
    private int timeIndex = 0;
    private boolean doShifting = false;

    public TimeGenerator() throws GeneratorException {
        LocalTime startTime = LocalTime.of(8,0);
        LocalTime endTime = LocalTime.of(16,0);
        int interval = 10;
        int daysBackFrom = 30;
        int daysBackTo = 1;

        buildTimeList(startTime, endTime, interval, daysBackFrom, daysBackTo);
    }

    public TimeGenerator(LocalTime startTime, LocalTime endTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {
        buildTimeList(startTime, endTime, interval, daysBackFrom, daysBackTo);
    }

    /***
     * Build a list of date/time values according to parameters
     * @param startTime     start of time window during a day where events occur
     * @param endTime       end of time window during a day where events occur
     * @param interval      interval betwee two events in minutes (events frequency)
     * @param daysBackFrom  how many days back from now will start the events
     * @param daysBackTo    how many days back from now will stop the events
     */
    protected void buildTimeList(LocalTime startTime, LocalTime endTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {

        if (interval <= 0) throw new GeneratorException("Interval must be greater than 0");

        // Initialize start and end time from current time and days back params
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        Instant start = utc.toInstant().minus(daysBackFrom, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        Instant end = utc.toInstant().minus(daysBackTo, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);

        Instant startTimeInDay = start.plus(startTime.getHour() * 60 + startTime.getMinute(), ChronoUnit.MINUTES);
        Instant endTimeInDay = start.plus(endTime.getHour() * 60 + endTime.getMinute(), ChronoUnit.MINUTES);
        Instant currentEvTime = startTimeInDay;


        // Loop from start to end time, increment bu interval
        while (currentEvTime.isBefore(end)) {
            // verify time interval in day scope
            if ((currentEvTime.isAfter(startTimeInDay) || currentEvTime.equals(startTimeInDay)) &&
                    currentEvTime.isBefore(endTimeInDay)) {
                eventTimes.add(currentEvTime);
            }
            currentEvTime = currentEvTime.plus(interval, ChronoUnit.MINUTES);

            // if current time is after endTimeInDay, need to move time interval to next day
            if (currentEvTime.isAfter(endTimeInDay)) {
                startTimeInDay = startTimeInDay.plus(1, ChronoUnit.DAYS);
                endTimeInDay = endTimeInDay.plus(1, ChronoUnit.DAYS);

                // rebuild start date
                if (!doShifting) currentEvTime = startTimeInDay;
            }
        }
   }

    public void printIntervals() {
        for (Instant et : eventTimes){
            System.out.println(et);
        }
    }

    public void reset() {
        timeIndex = 0;
    }

    @Override
    public boolean hasNext() {
        return (timeIndex < eventTimes.size());
    }

    @Override
    public Instant getNext() throws GeneratorException {
        if (!hasNext()) throw new GeneratorException ("Time Generator Exception occurred: End of the LocalTime interval is reached - no more data.");
        return eventTimes.get(timeIndex++);
    }

    public int getSize() {
        return eventTimes.size();
     }
    public Instant getFirst() {
        return eventTimes.get(0);
    }
    public Instant getLast() { return eventTimes.get(getSize()-1); }

    public void doShifting(boolean doShifting) {
        this.doShifting = doShifting;
    }
}
