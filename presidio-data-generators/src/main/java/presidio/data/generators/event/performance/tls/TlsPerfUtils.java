package presidio.data.generators.event.performance.tls;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Random;

import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.SATURDAY;
import static java.time.ZoneOffset.UTC;

public class TlsPerfUtils {

    private static final int millisInHour = 1000 * 60 * 60;

    public int activeHoursMillisBetweenEvents(int activeHours, double eventsPerDay ,  double offPeekRatio) {
        double activeHoursEventsPerDay = (1- offPeekRatio) * eventsPerDay;
        return (int) Math.round( (millisInHour * activeHours) / activeHoursEventsPerDay);
    }

    public int offpeekHoursMillisBetweenEvents(int activeHours, double eventsPerDay ,  double offPeekRatio) {
        double offHoursEventsPerDay = (offPeekRatio) * eventsPerDay;
        return (int) Math.round((millisInHour * (24 - activeHours)) / offHoursEventsPerDay);
    }

    public boolean needToSkipWeekendEvent(Instant time, Random random, double weekendSkipEventProbability) {
        LocalDate localDate = LocalDate.ofInstant(time, UTC);
        return  (localDate.getDayOfWeek().equals(FRIDAY) || localDate.getDayOfWeek().equals(SATURDAY))
                && random.nextDouble() <= weekendSkipEventProbability;
    }
}
