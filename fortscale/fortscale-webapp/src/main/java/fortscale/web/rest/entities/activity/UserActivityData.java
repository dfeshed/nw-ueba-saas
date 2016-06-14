package fortscale.web.rest.entities.activity;

import java.util.Objects;

/**
 * User Activity Data
 *
 * @author gils
 * 22/05/2016
 */
public class UserActivityData {

    public static class LocationEntry extends BaseLocationEntry {

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

    public static class AuthenticationsEntry {

        private int success;
        private int failed;

        public AuthenticationsEntry(int success, int failed) {
            this.success = success;
            this.failed = failed;
        }

        @Override
        public int hashCode() {
            return Objects.hash(success, failed);
        }

        @Override
        public boolean equals(Object other) {
            if (other == null) {
				return false;
			}
            if (other == this) {
				return true;
			}
            if (!(other instanceof AuthenticationsEntry)) {
				return false;
			}
            AuthenticationsEntry otherAuthenticationsEntry = (AuthenticationsEntry)other;
            return otherAuthenticationsEntry.success == success && otherAuthenticationsEntry.failed == failed;
        }

        public int getSuccess() {
            return success;
        }

        public void setSuccess(int success) {
            this.success = success;
        }

        public int getFailed() {
            return failed;
        }

        public void setFailed(int failed) {
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
        private float value;
        private String units;

        public DataUsageEntry(String dataEntityId, float value, String units) {
            this.dataEntityId = dataEntityId;
            this.value = value;
            this.units = units;
        }

    }

    public static enum DeviceType {
        Desktop,
        Mobile,
        Server,
		Windows,
		Linux,
		Mac
    }

}