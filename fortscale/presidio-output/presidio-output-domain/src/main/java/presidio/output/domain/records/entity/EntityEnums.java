package presidio.output.domain.records.entity;


import java.time.Period;
import java.util.stream.Stream;

public class EntityEnums {

    public enum Trends {

        weekly (Period.ofWeeks(1)), daily (Period.ofDays(1));

        private final Period period;

        Trends(Period period) {
            this.period = period;
        }

        public Period getPeriod() {
            return period;
        }

        public static Stream<Trends> stream() {
            return Stream.of(values());
        }

    }


}
