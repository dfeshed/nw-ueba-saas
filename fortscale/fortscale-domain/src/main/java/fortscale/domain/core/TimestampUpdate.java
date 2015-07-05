package fortscale.domain.core;

/**
 * Immutable Timestamp event
 */
public class TimestampUpdate {



    /** Time of evidence reading was taken. */
    private Long minimalTimeStamp;


    /**
     * Constructor.
     */
    public TimestampUpdate(Long minimalTimeStamp) {
        this.minimalTimeStamp = minimalTimeStamp;
    }

    public Long getMinimalTimeStamp() {
        return minimalTimeStamp;
    }
}
