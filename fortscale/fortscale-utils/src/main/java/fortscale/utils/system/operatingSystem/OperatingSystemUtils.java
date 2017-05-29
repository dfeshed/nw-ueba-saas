package fortscale.utils.system.operatingSystem;

/**
 * Created by efratn on 29/05/2017.
 */
public class OperatingSystemUtils {

    public String getOperatingSystem() {
        return System.getProperty("os.name");
    }

    public boolean isWinOperatingSystem() {
        return getOperatingSystem().toLowerCase().startsWith("win");
    }
}
