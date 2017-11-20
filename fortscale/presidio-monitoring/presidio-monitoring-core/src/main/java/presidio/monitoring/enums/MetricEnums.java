package presidio.monitoring.enums;


import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MetricEnums {

    public enum MetricValues {
        DEFAULT_METRIC_VALUE("metric_value"), SUM("sum"), MAX("max"), AVG("avg"), COUNT("count");

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
            set.add(fromValue("metric_value"));
            set.add(fromValue("count"));
            return set;
        }
    }

    public enum MetricTagKeysEnum {
        HOST, SCHEMA, UNIT, RESULT, APPLICATION_NAME, PID, DATE;
    }

    public enum MetricUnitType {
        DEFAULT_METRIC_TYPE("metric_type"), NUMBER("number"), KB("kilo_byte"), MB("mega_byte"), GB("giga_byte"), MILLI_SECOND("milli_second"), SECOND("second"), DATE("date");

        private String value;

        MetricUnitType(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }

        public static MetricUnitType fromValue(String text) {
            for (MetricUnitType b : MetricUnitType.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

    }
}
