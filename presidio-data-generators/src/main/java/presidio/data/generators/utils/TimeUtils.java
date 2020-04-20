package presidio.data.generators.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Created by YaronDL on 7/9/2017.
 */
public class TimeUtils {

    public static String calcDaysBack(int daysBack) {
        final DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.of("UTC"));

        return formatter.format(Instant.now().minus(daysBack, ChronoUnit.DAYS));
    }

//    public static Instant getInstant(int daysback, String timeInDay){
//       // + "T10:00:00.00Z"
//    }
}
