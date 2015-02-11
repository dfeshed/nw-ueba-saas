package fortscale.streaming.service.vpn;

/**
 * Created by rans on 03/02/15.
 */
public class VpnDataBucketsConfig {
    //Data Buckets fields
    private String totalbytesFieldName;
    private String readbytesFieldName;
    private String durationFieldName;
    private String databucketFieldName;

    public VpnDataBucketsConfig(String totalbytesFieldName, String readbytesFieldName, String durationFieldName, String databucketFieldName) {
        this.totalbytesFieldName = totalbytesFieldName;
        this.readbytesFieldName = readbytesFieldName;
        this.durationFieldName = durationFieldName;
        this.databucketFieldName = databucketFieldName;
    }

    public String getTotalbytesFieldName() {
        return totalbytesFieldName;
    }

    public void setTotalbytesFieldName(String totalbytesFieldName) {
        this.totalbytesFieldName = totalbytesFieldName;
    }

    public String getReadbytesFieldName() {
        return readbytesFieldName;
    }

    public void setReadbytesFieldName(String readbytesFieldName) {
        this.readbytesFieldName = readbytesFieldName;
    }

    public String getDurationFieldName() {
        return durationFieldName;
    }

    public void setDurationFieldName(String durationFieldName) {
        this.durationFieldName = durationFieldName;
    }

    public String getDatabucketFieldName() {
        return databucketFieldName;
    }

    public void setDatabucketFieldName(String databucketFieldName) {
        this.databucketFieldName = databucketFieldName;
    }
}
