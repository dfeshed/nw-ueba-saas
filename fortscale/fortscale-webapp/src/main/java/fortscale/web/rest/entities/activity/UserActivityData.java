package fortscale.web.rest.entities.activity;

/**
 * User Activity Data
 *
 * @author gils
 * 22/05/2016
 */
public class UserActivityData {

    public static class LocationEntry extends BaseLocationEntry{
        public LocationEntry(String country, int count) {
            super(country, count);
        }
    }

    public static class SourceDeviceEntry {
        private String deviceName;
        private int count;
        private DeviceType deviceType;

        public SourceDeviceEntry(String deviceName, int count, DeviceType deviceType) {
            this.deviceName = deviceName;
            this.count = count;
            this.deviceType = deviceType;
        }
    }

    public static class TargetDeviceEntry {
        private String deviceName;
        private int count;

        public TargetDeviceEntry(String deviceName, int count) {
            this.deviceName = deviceName;
            this.count = count;
        }
    }

    public static class Authentications {
        private int success;
        private int failed;

        public Authentications(int success, int failed) {
            this.success = success;
            this.failed = failed;
        }
    }

    public static class WorkingHourEntry {
        private int hour;

        public WorkingHourEntry(int hour) {
            this.hour = hour;
        }
    }

    public static class DataUsageEntry {
        private String dataEntityId;
        private int value;
        private String units;

        public DataUsageEntry(String dataEntityId, int value, String units) {
            this.dataEntityId = dataEntityId;
            this.value = value;
            this.units = units;
        }
    }

    public static enum DeviceType {
        Desktop,
        Mobile,
        Server
    }

    static class BaseLocationEntry {
        private String country;
        private int count;

        BaseLocationEntry(String country, int count) {
            this.country = country;
            this.count = count;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public int getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }
    }

}
