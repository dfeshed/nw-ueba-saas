package fortscale.services.users.util.activity;

import java.util.Objects;

/**
 * User Activity Data
 *
 * @author gils
 * 22/05/2016
 */
public class UserActivityData {

    public interface  BaseUserActivityEntry{

    }

    public static class LocationEntry extends fortscale.services.users.util.activity.BaseLocationEntry implements  BaseUserActivityEntry {
        public LocationEntry(String country, double count) {
            super(country, count);
        }
    }

    public static class DeviceEntry implements  BaseUserActivityEntry {
        private String deviceName;
        private double count;
        private DeviceType deviceType;

        public DeviceEntry(String deviceName, double count, DeviceType deviceType) {
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

    public static class NameCountTypeEntry implements  BaseUserActivityEntry{
        private String name;
        private int count;
        private String type;

        public NameCountTypeEntry() {
        }

        public NameCountTypeEntry(String name, int count, String type) {
            this.name = name;
            this.count = count;
            this.type = type;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

  /*  public static class TargetDeviceEntry  implements  BaseUserActivityEntry {

        private String deviceName;
        private double count;

        public TargetDeviceEntry(String deviceName, double count) {
            this.deviceName = deviceName;
            this.count = count;
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
    }
*/
    public static class AuthenticationsEntry  implements  BaseUserActivityEntry {

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

    public static class WorkingHourEntry  implements  BaseUserActivityEntry {

        private double hour;

        public WorkingHourEntry(double hour) {
        
            this.hour = hour;
        }

        public double getHour() {
            return hour;
        }

        public void setHour(double hour) {
            this.hour = hour;
        }
    }

	public static class DataUsageEntry  implements  BaseUserActivityEntry {

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