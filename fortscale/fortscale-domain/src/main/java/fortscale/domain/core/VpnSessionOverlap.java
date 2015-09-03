package fortscale.domain.core;

/**
 * Created by Amir Keren on 03/09/15.
 */
public class VpnSessionOverlap {

    private long date_time_unix;
    private long duration;
    private String source_ip;

    public long getDate_time_unix() {
        return date_time_unix;
    }

    public void setDate_time_unix(long date_time_unix) {
        this.date_time_unix = date_time_unix;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getSource_ip() {
        return source_ip;
    }

    public void setSource_ip(String source_ip) {
        this.source_ip = source_ip;
    }

}