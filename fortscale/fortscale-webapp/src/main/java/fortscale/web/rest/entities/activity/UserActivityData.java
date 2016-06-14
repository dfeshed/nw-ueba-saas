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

        public LocationEntry(String country, double count) {
            super(country, count);
        }

    }

    public static class SourceDeviceEntry {

        private String deviceName;
        private double count;
        private DeviceType deviceType;

        public SourceDeviceEntry(String deviceName, double count, DeviceType deviceType) {
            this.deviceName = deviceName;
            this.count = count;
            this.deviceType = deviceType;
        }

    }

    public static class TargetDeviceEntry {

        private String deviceName;
        private double count;

        public TargetDeviceEntry(String deviceName, double count) {
            this.deviceName = deviceName;
            this.count = count;
        }

    }

    public static class AuthenticationsEntry {

        private double success;
        private double failed;

        public AuthenticationsEntry(double success, double failed) {
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

        public double getSuccess() {
            return success;
        }

        public void setSuccess(double success) {
            this.success = success;
        }

        public double getFailed() {
            return failed;
        }

        public void setFailed(double failed) {
            this.failed = failed;
        }

    }

    public static class WorkingHourEntry {

        private double hour;

        public WorkingHourEntry(double hour) {
            this.hour = hour;
        }

    }

    public static class DataUsageEntry {

        private String dataEntityId;
        private double value;
        private String units;

        public DataUsageEntry(String dataEntityId, double value, String units) {
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