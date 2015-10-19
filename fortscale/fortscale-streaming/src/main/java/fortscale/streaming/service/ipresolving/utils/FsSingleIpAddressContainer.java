package fortscale.streaming.service.ipresolving.utils;

/**
 * Created by shays on 15/10/2015.
 */

/**
 * Implementes FsIpAddressContainer.
 * Used to contain single IP address
 */
public class FsSingleIpAddressContainer implements FsIpAddressContainer {

    private String ipAddress;

    public FsSingleIpAddressContainer(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isMatch(String sourceIp){
        long sourceIpValue = FsIpAddressUtils.ipToLong(sourceIp);
        long ipAddressValue = FsIpAddressUtils.ipToLong(this.ipAddress);
        return sourceIpValue == ipAddressValue;

    }


}
