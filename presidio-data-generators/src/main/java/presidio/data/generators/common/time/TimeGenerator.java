package presidio.data.generators.common.time;

import presidio.data.generators.common.GeneratorException;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TimeGenerator implements ITimeGenerator {

    private LocalTime startLocalTime;
    private LocalTime endLocalTime;
    private int interval;
    private int daysBackFrom;
    private int daysBackTo;
    private Instant startInstant;
    private Instant endInstant;
    private Integer size = null;

    TimeGeneratorIterator iterator;

    public TimeGenerator() throws GeneratorException {
        this(LocalTime.of(8,0), LocalTime.of(16,0), 10, 30, 1);
    }

    public TimeGenerator(LocalTime startLocalTime, LocalTime endLocalTime, int interval, int daysBackFrom, int daysBackTo) throws GeneratorException {
        if (interval <= 0) throw new GeneratorException("Interval must be greater than 0");
        this.startLocalTime = startLocalTime;
        this.endLocalTime = endLocalTime;
        this.interval = interval;
        this.daysBackFrom = daysBackFrom;
        this.daysBackTo = daysBackTo;

        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        Instant startDayInstant = utc.toInstant().minus(daysBackFrom, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        this.startInstant = startDayInstant.plus(startLocalTime.toNanoOfDay(), ChronoUnit.NANOS);
        this.endInstant = calcEndInstantInDay(utc.toInstant().minus(daysBackTo+1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS));
        
        this.iterator = new TimeGeneratorIterator();
    }

    /***
     * Build a list of all Instant values that the time generator will output.
     */
    protected List<Instant> buildTimeList(){
        List<Instant> eventTimes = new ArrayList<>();
        TimeGeneratorIterator iterator = new TimeGeneratorIterator();

        while(iterator.hasNext() != null){
            eventTimes.add(iterator.getNext());
        }
        return eventTimes;
   }

    public void printIntervals() {
        TimeGeneratorIterator iterator = new TimeGeneratorIterator();
        
        while(iterator.hasNext() != null){
            System.out.println(iterator.getNext());
        }
    }

    public void reset() {
        iterator = new TimeGeneratorIterator();
    }

    @Override
    public Instant hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Instant getNext() throws GeneratorException {
        if (hasNext() == null) throw new GeneratorException ("Time Generator Exception occurred: End of the LocalTime interval is reached - no more data.");
        return iterator.getNext();
    }

    public int getSize() {
        if(size == null){
            TimeGeneratorIterator iterator = new TimeGeneratorIterator();
            int cnt = 0;
            while(iterator.hasNext() != null){
                cnt++;
                iterator.advanceNextInstant();
            }
            size = cnt;
        }
        
        return size;
     }
     
    public Instant getFirst() {
        return startInstant;
    }
    public Instant getLast() { 
        return endInstant; 
    }
    
    private Instant calcEndInstantInDay(Instant startDay){
        return startDay.plus(endLocalTime.toNanoOfDay(), ChronoUnit.NANOS);
    }
    
    public class TimeGeneratorIterator{
        private Instant nextInstant;
        private Instant startInstantInCurDay;
        private Instant endInstantInCurDay;
        
        public TimeGeneratorIterator(){
            nextInstant = startInstant;
            startInstantInCurDay = startInstant;
            endInstantInCurDay = calcEndInstantInDay(startInstant.truncatedTo(ChronoUnit.DAYS));
        }
        
        public Instant hasNext(){
            return nextInstant;
        }
        
        public Instant getNext(){
            Instant ret = nextInstant;
            advanceNextInstant();
            return ret;
        }
        
        private void advanceNextInstant(){
            if(nextInstant == null){
                return;
            }
            
            nextInstant = nextInstant.plus(interval, ChronoUnit.MILLIS);

            // if current time is after endTimeInDay, need to move time interval to next day
            if (nextInstant.getEpochSecond() >= endInstantInCurDay.getEpochSecond()) {
                startInstantInCurDay = startInstantInCurDay.plus(1, ChronoUnit.DAYS);
                endInstantInCurDay = endInstantInCurDay.plus(1, ChronoUnit.DAYS);

                nextInstant = startInstantInCurDay;
            }
            if(nextInstant.getEpochSecond() >= endInstant.getEpochSecond()){
                nextInstant = null;
            }
        }
    }
}
