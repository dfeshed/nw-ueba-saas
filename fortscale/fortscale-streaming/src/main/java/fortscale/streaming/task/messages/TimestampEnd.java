package fortscale.streaming.task.messages;

/**
 * Immutable Temperature Event class. The process control system creates these events. The
 * TemperatureEventHandler picks these up and processes them.
 */
public class TimestampEnd {



    /** Time temerature reading was taken. */
    private Long minimalTimeStamp;
private int sessionId;

    /**
     * Temeratur constructor.
     */
    public TimestampEnd(Long minimalTimeStamp, int sessionId) {
        this.minimalTimeStamp = minimalTimeStamp;
        this.sessionId = sessionId;
    }

    public Long getMinimalTimeStamp() {
        return minimalTimeStamp;
    }

    public int getSessionId() {
        return sessionId;
    }
}
