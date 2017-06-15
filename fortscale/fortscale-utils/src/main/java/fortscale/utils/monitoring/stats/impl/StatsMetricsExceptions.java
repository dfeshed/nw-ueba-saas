package fortscale.utils.monitoring.stats.impl;

/**
 *
 * A container class for all stats monitoring implementation exceptions
 *
 * Created by gaashh on 4/12/16.
 */
public class StatsMetricsExceptions {

    /**
     * A base class for all stats monitoring implementation exceptions
     */
    static public class StatsMetricsBaseException extends RuntimeException {

        /**
         * ctor for exception with a message
         *
         * @param msg
         */
        public StatsMetricsBaseException(String msg) {
            super(msg);
        }

        /**
         * ctor for exception with a message and cause exception (used inside catch blocks)
         *
         * @param msg
         */
        public StatsMetricsBaseException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

    /**
     * A problem while registering metrics group had obscured
     */
    static public class ProblemWhileRegisteringMetricsGroupException extends StatsMetricsBaseException {

        public ProblemWhileRegisteringMetricsGroupException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }


    /**
     * Failed to read field value
     */
    static public class FailedToReadFieldValueException extends StatsMetricsBaseException {

        public FailedToReadFieldValueException(String msg, Throwable cause) {
            super(msg, cause);
        }

    }

    /**
     * Unsupported data type
     */
    static public class UnsupportedDataTypeException extends StatsMetricsBaseException {

        public UnsupportedDataTypeException(String msg) {
            super(msg);
        }

    }

    /**
     * Metric name already exists
     */
    static public class MetricNameAlreadyExistsException extends StatsMetricsBaseException {

        public MetricNameAlreadyExistsException(String msg) {
            super(msg);
        }

    }


}
