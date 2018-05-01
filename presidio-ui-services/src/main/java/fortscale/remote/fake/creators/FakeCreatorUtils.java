package fortscale.remote.fake.creators;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class FakeCreatorUtils {

    BigDecimal timeStringToEpochBig(String time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return new BigDecimal(LocalDateTime.parse(time,formatter).toEpochSecond(ZoneOffset.UTC));
    }
}
