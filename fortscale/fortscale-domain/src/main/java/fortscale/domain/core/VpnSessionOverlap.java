package fortscale.domain.core;

/**
 * Created by Amir Keren on 03/09/15.
 */
public class VpnSessionOverlap {

    private long date_time_unix;
    private long duration;
    private String source_ip;
    private String local_ip;
    private long totalbytes;
    private double bytesavg;
    private String hostname;
    private String country;

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

    public String getLocal_ip() {
        return local_ip;
    }

    public void setLocal_ip(String local_ip) {
        this.local_ip = local_ip;
    }

    public long getTotalbytes() {
        return totalbytes;
    }

    public void setTotalbytes(long totalbytes) {
        this.totalbytes = totalbytes;
    }

    public double getBytesavg() {
        return bytesavg;
    }

    public void setBytesavg(double bytesavg) {
        this.bytesavg = bytesavg;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}