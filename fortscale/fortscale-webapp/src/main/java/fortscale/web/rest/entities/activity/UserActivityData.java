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

        public String getDeviceName() {
            return deviceName;
        }

        public void setDeviceName(String deviceName) {
            this.deviceName = deviceName;
        }

        public double getCount() {
            return count;
        }

        public void setCount(double count) {
            this.count = count;
        }

        public DeviceType getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(DeviceType deviceType) {
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

        public void setSuccess(int success) {
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
		private int days;

		public DataUsageEntry(String dataEntityId, double value, int days) {
			this.dataEntityId = dataEntityId;
			this.value = value;
			this.days = days;
		}

		public double getValue() {
			return value;
		}

		public void setValue(double value) {
			this.value = value;
		}

		public int getDays() {
			return days;
		}

		public void setDays(int days) {
			this.days = days;
		}

		public String getDataEntityId() {
			return dataEntityId;
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