package fortscale.domain.core;

/**
 * Immutable Temperature Event class. The process control system creates these events. The
 * TemperatureEventHandler picks these up and processes them.
 */
public class TimestampUpdate {



    /** Time temerature reading was taken. */
    private Long minimalTimeStamp;


    /**
     * Temeratur constructor.
     */
    public TimestampUpdate(Long minimalTimeStamp) {
        this.minimalTimeStamp = minimalTimeStamp;
    }

    public Long getMinimalTimeStamp() {
        return minimalTimeStamp;
    }
}
