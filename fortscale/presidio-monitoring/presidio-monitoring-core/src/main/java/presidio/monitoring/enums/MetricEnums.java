package presidio.monitoring.enums;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MetricEnums {

    public enum MetricValues {
        SUM("sum"), MAX("max"), AVG("avg"), COUNT("count");

        private String value;

        MetricValues(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static MetricValues fromValue(String text) {
            for (MetricValues b : MetricValues.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static Collection<MetricValues> collectionOfMetricValues() {
            Set<MetricValues> set = new HashSet<>();
            set.add(fromValue("sum"));
            set.add(fromValue("max"));
            set.add(fromValue("avg"));
            return set;
        }
    }
}
