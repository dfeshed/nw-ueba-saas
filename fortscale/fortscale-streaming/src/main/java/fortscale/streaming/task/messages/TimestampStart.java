package fortscale.streaming.task.messages;

/**
 * Immutable Temperature Event class. The process control system creates these events. The
 * TemperatureEventHandler picks these up and processes them.
 */
public class TimestampStart {



    /** Time temerature reading was taken. */
    private Long minimalTimeStamp;


    /**
     * Temeratur constructor.
     */
    public TimestampStart(Long minimalTimeStamp) {
        this.minimalTimeStamp = minimalTimeStamp;
    }

    public Long getMinimalTimeStamp() {
        return minimalTimeStamp;
    }
}
